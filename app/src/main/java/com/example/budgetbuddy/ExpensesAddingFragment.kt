package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.Editable.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.budgetbuddy.databinding.ActivityRegistrationBinding
import com.example.budgetbuddy.databinding.FragmentExpensesAddingBinding
import com.example.budgetbuddy.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import android.widget.LinearLayout;



private const val ARG_SELECTED_DATE = "selected_date"
private const val ARG_SELECTED_CATEGORY = "selected_category"
private const val ARG_AMOUNT = "selected_amount"
private const val ARG_NOTES = "selected_notes"
class ExpensesAddingFragment : Fragment() {


    private lateinit var binding: FragmentExpensesAddingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentExpensesAddingBinding.inflate(inflater, container, false)
        arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
            binding.selectDateEnd.text = selectedDate
        }
        arguments?.getString(ARG_SELECTED_CATEGORY)?.let { selectedCategory ->
            binding.chooseCategoryButton.text = selectedCategory
        }

        arguments?.getString(ARG_NOTES)?.let { selectedNotes->
            binding.noteEditText.text=  Factory.getInstance().newEditable(selectedNotes.toString())
        }




        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

            if (selectedAmount != 0.0) {
                val formattedAmount = String.format("%.2f", selectedAmount)
                binding.amount.text = Factory.getInstance().newEditable(formattedAmount)
            }
        }

        binding.undo.setOnClickListener{
            val home = HomeFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, home)
            transaction.addToBackStack(null)
            transaction.commit()

        }



        binding.chooseCalendarButton.setOnClickListener {

            val amountString = binding.amount.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0
            val calendarFragment = CalendarFragment.newInstance("val1",binding.chooseCategoryButton.text.toString(),binding.noteEditText.text.toString(),amount)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.today.setOnClickListener{
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH) + 1
            val year = currentDate.get(Calendar.YEAR)

            binding.selectDateEnd.text = String.format("%02d.%02d.%d", day, month, year)

        }

        binding.yesterday.setOnClickListener{
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_MONTH, -1)
            val dayYesterday = yesterday.get(Calendar.DAY_OF_MONTH)
            val month = yesterday.get(Calendar.MONTH) + 1
            val year = yesterday.get(Calendar.YEAR)

            binding.selectDateEnd.text = String.format("%02d.%02d.%d", dayYesterday, month, year)

        }

        binding.chooseCategoryButton.setOnClickListener{

            val amountString = binding.amount.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0

            val categoryFragment = CategorySelectFragment.newInstance(binding.selectDateEnd.text.toString(),"val2",binding.noteEditText.text.toString(),amount)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, categoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        val budgetRef = db.collection(userId).document("budget")

        binding.confirmButton2.setOnClickListener {
            val amount = binding.amount.text.toString().toDoubleOrNull()
            val date = binding.selectDateEnd.text.toString()
            val category = binding.chooseCategoryButton.text.toString()
            val notes = binding.noteEditText.text.toString()

            val budgetRef = db.collection(userId).document("budget")

            if( amount != null && date.isNotEmpty() && category.isNotEmpty() && notes.isNotEmpty()) {
                val expenseMap = hashMapOf(
                    "amount" to -amount,
                    "date" to date,
                    "notes" to notes
                )
                budgetRef.collection(category).document().set(expenseMap).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Twój wydatek został dodany", Toast.LENGTH_SHORT).show()


                    budgetRef.get().addOnSuccessListener { document ->
                        val currentBudget = document.getDouble("budgetV") ?: 0.0
                        val updatedBudget = currentBudget - amount

                        budgetRef.update("budgetV", updatedBudget).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Budżet został zaktualizowany", Toast.LENGTH_SHORT).show()

                            val home = HomeFragment.newInstance()
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, home)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Wystąpił błąd podczas aktualizacji budżetu", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Wystąpił błąd podczas pobierania aktualnego budżetu", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Wystąpił błąd podczas dodawania wydatku", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }


        val view=binding.root
        return view
    }

    companion object {

        fun newInstance(selectedDate: String?=null, selectedCategory:String?=null, selectedNotes:String?=null, selectedAmount:Double?=0.0 ) =
            ExpensesAddingFragment().apply {
                arguments = Bundle().apply {

                    if (selectedDate != null ) {
                        putString(ARG_SELECTED_DATE, selectedDate)
                    }
                    if (selectedCategory != null ) {
                        putString(ARG_SELECTED_CATEGORY, selectedCategory)
                    }
                    if (selectedNotes != null ) {
                        putString(ARG_NOTES, selectedNotes)
                    }
                    if (selectedAmount != null) {
                        putDouble(ARG_AMOUNT, selectedAmount)
                    }



                }
            }
    }
}