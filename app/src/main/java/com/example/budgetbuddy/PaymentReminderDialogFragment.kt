package com.example.budgetbuddy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PaymentReminderDialogFragment : DialogFragment()  {




    private var db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FixedExpenseAdapter
    private var ifOpen :Boolean?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_reminder_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButton = view.findViewById<Button>(R.id.confirm_zlecenie_stale)
        val cancelButton = view.findViewById<Button>(R.id.no_confirm_zlecenie_stale)

        recyclerView = view.findViewById(R.id.recycler_viewre)
        adapter = FixedExpenseAdapter(arrayListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        getFixedExpenses()


        confirmButton.setOnClickListener {
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }



    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    private fun getFixedExpenses() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val budgetRef = db.collection(userId).document("fixedExpenses").collection("documents")

        budgetRef.get()
            .addOnSuccessListener { documents ->
                val fixedExpenseList = arrayListOf<ModelReminderItem>()
                val today = Calendar.getInstance().time

                for (document in documents) {
                    val fixedExpense = document.toObject(ModelReminderItem::class.java)
                    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    var nextDate: Date? = null

                    try {
                        if (fixedExpense.nextDate.isNotEmpty()) {
                            nextDate = dateFormatter.parse(fixedExpense.nextDate)
                        }
                    } catch (e: ParseException) {
                        Log.e("PaymentReminderDialog", "Error date: ${fixedExpense.nextDate}", e)
                        continue
                    }

                    val repeatFrequency = fixedExpense.repeatFrequency
                    var newNextDate=dateFormatter.format(nextDate!!)
                    while (nextDate != null && ( nextDate < today || isSameDay(today,nextDate) )) {

                        val updatedItem = ModelReminderItem(
                            name = fixedExpense.name,
                            amount = fixedExpense.amount,
                            nextDate = newNextDate,
                            isChecked = fixedExpense.isChecked,
                            repeatFrequency = fixedExpense.repeatFrequency
                        )
                        newNextDate = calculateNewNextDate(nextDate, repeatFrequency)
                        fixedExpenseList.add(updatedItem)
                        updateNextDateInDatabase(document.id, newNextDate)
                        nextDate = dateFormatter.parse(newNextDate)
                        Log.d("NewNextDate", "New Next Date: $newNextDate")
                    }
                }

                if (fixedExpenseList.isNotEmpty()) {
                    ifOpen = true
                    adapter.updateList(fixedExpenseList)
                } else {
                    dismiss()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Błąd wyświetlania zleceń stałych", Toast.LENGTH_SHORT).show()
            }
    }



    private fun updateNextDateInDatabase(documentId: String, newNextDate: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val fixedExpensesRef = db.collection(userId).document("fixedExpenses").collection("documents")

        fixedExpensesRef.document(documentId)
            .update("nextDate", newNextDate)
            .addOnSuccessListener {
                Log.d("NewNextDate", "Data zaaktualizowana")
            }
            .addOnFailureListener {
                Toast.makeText(context, "Data nie zaaktualizowana", Toast.LENGTH_SHORT).show()

            }
    }
    private fun calculateNewNextDate(currentNextDate: Date?, repeat: String): String {
        val calendar = Calendar.getInstance()
        calendar.time = currentNextDate ?: Date()

        when (repeat) {
            "codziennie" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            "co tydzień" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "co dwa tygodnie" -> calendar.add(Calendar.WEEK_OF_YEAR, 2)
            "co miesiąc" -> calendar.add(Calendar.MONTH, 1)
            "co trzy miesiące" -> calendar.add(Calendar.MONTH, 3)
            "co sześć miesięcy" -> calendar.add(Calendar.MONTH, 6)
            "co rok" -> calendar.add(Calendar.YEAR, 1)
        }

        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return String.format(Locale.getDefault(), "%02d.%02d.%d", dayOfMonth, month, year)
    }



}