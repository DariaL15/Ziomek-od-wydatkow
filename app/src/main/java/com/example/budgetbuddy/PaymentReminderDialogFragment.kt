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
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

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

            val selectedExpenses = adapter.getSelectedExpenses()
            selectedExpenses.forEach { selectedExpense ->
                val amount = selectedExpense.amount.toString().toDoubleOrNull()
                val date = selectedExpense.nextDate
                val category = selectedExpense.category
                val notes = selectedExpense.name
                val budgetRef = db.collection(userId).document("budget")
                if (amount != null && date.isNotEmpty() && category.isNotEmpty() && notes.isNotEmpty() && category != "choose") {
                    val expenseMap = hashMapOf(
                        "amount" to -amount,
                        "date" to date,
                        "notes" to notes
                    )

                    budgetRef.collection(category).document().set(expenseMap).addOnSuccessListener {
                        Log.d("PaymentReminderDialog", "Twój wydatek dodany z zlecenia stałego")


                        budgetRef.get().addOnSuccessListener { document ->
                            val currentBudget = document.getDouble("budgetV") ?: 0.0
                            val updatedBudget = currentBudget - amount
                            val currentExpenses = document.getDouble("expensesV") ?: 0.0
                            val updatedExpenses = currentExpenses + amount

                            budgetRef.update(
                                mapOf(
                                    "budgetV" to updatedBudget,
                                    "expensesV" to updatedExpenses
                                )
                            ).addOnSuccessListener {
                                Log.d("PaymentReminderDialog", "Budżet zaaktualizowany (zlecenie stałe) " )


                            }.addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Wystąpił błąd podczas aktualizacji budżetu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Wystąpił błąd podczas pobierania aktualnego budżetu",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Wystąpił błąd podczas dodawania wydatku",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            val home = HomeFragment.newInstance()
            val transaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, home)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        cancelButton.setOnClickListener {
            val home = HomeFragment.newInstance()
            val transaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, home)
            transaction.addToBackStack(null)
            transaction.commit()
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
                    var endDate: Date?= null
                    try {
                        if (fixedExpense.nextDate.isNotEmpty()) {
                            nextDate = dateFormatter.parse(fixedExpense.nextDate)
                        }
                    } catch (e: ParseException) {
                        Log.e("PaymentReminderDialog", "Błąd kolejnej daty zlecenia", e)
                        continue
                    }

                    try {
                        if (fixedExpense.endDayOfTransfers.isNotEmpty()) {
                            endDate = dateFormatter.parse(fixedExpense.endDayOfTransfers)
                        }
                    } catch (e: ParseException) {
                        Log.e("PaymentReminderDialog", "Błąd pobierania daty zakończenia zlecenia", e)
                        continue
                    }

                    val repeatFrequency = fixedExpense.repeatFrequency
                    var newNextDate=dateFormatter.format(nextDate!!)

                    while (nextDate != null && ( nextDate < today || isSameDay(today,nextDate) ) ) {

                        val updatedItem = ModelReminderItem(
                            name = fixedExpense.name,
                            amount = fixedExpense.amount,
                            nextDate = newNextDate,
                            isChecked = fixedExpense.isChecked,
                            repeatFrequency = fixedExpense.repeatFrequency,
                            category = fixedExpense.category


                        )
                        if(fixedExpense.amountOfTransfers !=0 && fixedExpense.amountOfTransfersTemp != 0)
                        {
                            fixedExpense.amountOfTransfersTemp -= 1
                            updateAmountOfTransfersTempInDatabase(document.id, fixedExpense.amountOfTransfersTemp)
                        }
                        if(fixedExpense.amountOfTransfers !=0 && fixedExpense.amountOfTransfersTemp == 0)
                        {
                            budgetRef.document(document.id).delete()
                        }

                        if(endDate!=null && nextDate >= endDate)
                        {
                            budgetRef.document(document.id).delete()
                        }

                        newNextDate = calculateNewNextDate(nextDate, repeatFrequency)
                        fixedExpenseList.add(updatedItem)
                        updateNextDateInDatabase(document.id, newNextDate)
                        nextDate = dateFormatter.parse(newNextDate)
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
                Log.d("NewNextDate", "Data nie zaaktualizowana")

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

    private fun updateAmountOfTransfersTempInDatabase(documentId: String, newAmount: Int) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val fixedExpensesRef = db.collection(userId).document("fixedExpenses").collection("documents")

        fixedExpensesRef.document(documentId)
            .update("amountOfTransfersTemp", newAmount)
            .addOnSuccessListener {
                Log.d("UpdateAmount", "amountOfTransfersTemp zaaktualizowana")
            }
            .addOnFailureListener { e ->
                Log.w("UpdateAmount", "amountOfTransfersTemp nie zaaktualizowana", e)
            }
    }



}