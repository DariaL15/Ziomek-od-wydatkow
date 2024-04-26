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
                    val today = Calendar.getInstance().time
                    val differenceInDays = calculateHowManyDays(nextDate, repeatFrequency)


                    if (nextDate != null && ( isSameDay(today, nextDate) || !differenceInDays ) && nextDate<today) {

                            fixedExpenseList.add(fixedExpense)
                            val newNextDate = calculateNewNextDate(nextDate, repeatFrequency)
                            updateNextDateInDatabase(document.id, newNextDate)
                    }
                }

                if (fixedExpenseList.isNotEmpty()) {

                    ifOpen =true
                    adapter.updateList(fixedExpenseList)
                }
                else
                {
                    dismiss()
                }


            }
            .addOnFailureListener {
                Toast.makeText(context, "Błąd wyświtlania zleceń stałych", Toast.LENGTH_SHORT).show()
            }
    }




    private fun updateNextDateInDatabase(documentId: String, newNextDate: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val fixedExpensesRef = db.collection(userId).document("fixedExpenses").collection("documents")

        fixedExpensesRef.document(documentId)
            .update("nextDate", newNextDate)
            .addOnSuccessListener {
                Toast.makeText(context, "Data zaaktualizowana", Toast.LENGTH_SHORT).show()
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
        val newNextDateFormatted = String.format(Locale.getDefault(), "%02d.%02d.%d", dayOfMonth, month, year)
        Log.d("NewNextDate", "New Next Date: $newNextDateFormatted")
        return newNextDateFormatted
    }


    private fun calculateHowManyDays(currentNextDate: Date?, repeat: String): Boolean {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        calendar.time = currentNextDate ?: Date()

        val daysDifference = when (repeat) {
            "codziennie" -> 1
            "co tydzień" -> 7
            "co dwa tygodnie" -> 14
            "co miesiąc" -> {
                val nextMonth = today.clone() as Calendar
                nextMonth.add(Calendar.MONTH, 1)
                val daysInMonth = nextMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                daysInMonth
            }
            "co trzy miesiące" -> {
                val nextQuarter = today.clone() as Calendar
                nextQuarter.add(Calendar.MONTH, 3)
                val daysInQuarter = nextQuarter.getActualMaximum(Calendar.DAY_OF_MONTH)
                daysInQuarter
            }
            "co sześć miesięcy" -> {
                val nextHalfYear = today.clone() as Calendar
                nextHalfYear.add(Calendar.MONTH, 6)
                val daysInHalfYear = nextHalfYear.getActualMaximum(Calendar.DAY_OF_MONTH)
                daysInHalfYear
            }
            "co rok" -> {
                val nextYear = today.clone() as Calendar
                nextYear.add(Calendar.YEAR, 1)
                val daysInYear = nextYear.getActualMaximum(Calendar.DAY_OF_YEAR)
                daysInYear
            }

            else -> {
                0
            }
        }

        val differenceInDays = (calendar.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)

        return if (differenceInDays < daysDifference) {
             false
        } else {
             true
        }
    }



}