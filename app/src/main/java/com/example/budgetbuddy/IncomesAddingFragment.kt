package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.budgetbuddy.databinding.FragmentExpensesAddingBinding
import com.example.budgetbuddy.databinding.FragmentIncomesAddingBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


private const val ARG_SELECTED_DATE = "selected_date"
private const val ARG_SELECTED_CATEGORY = "selected_category"
private const val ARG_AMOUNT = "selected_amount"
private const val ARG_NOTES = "selected_notes"

class IncomesAddingFragment : Fragment() {

    private lateinit var binding: FragmentIncomesAddingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        binding = FragmentIncomesAddingBinding.inflate(inflater, container, false)

        binding.addExpensesButton1.setOnClickListener {
            val expensesAddingFragment = ExpensesAddingFragment.newInstance(null,null,null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, expensesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
            binding.selectDateEnd1.text = selectedDate
        }

        var categoryVal = ""
        arguments?.getString(ARG_SELECTED_CATEGORY)?.let { selectedCategory ->

            categoryVal = selectedCategory
            if(categoryVal == "car") {binding.chooseCategoryButton1.text="samochód"}
            if(categoryVal == "house") {binding.chooseCategoryButton1.text="dom"}
            if(categoryVal == "clothes") {binding.chooseCategoryButton1.text="ubrania"}
            if(categoryVal == "shopping") {binding.chooseCategoryButton1.text="zakupy"}
            if(categoryVal == "transport") {binding.chooseCategoryButton1.text="transport"}
            if(categoryVal == "sport") {binding.chooseCategoryButton1.text="sport"}
            if(categoryVal == "health") {binding.chooseCategoryButton1.text="zdrowie"}
            if(categoryVal == "entertaiment") {binding.chooseCategoryButton1.text="rozrywka"}
            if(categoryVal == "relax") {binding.chooseCategoryButton1.text="relax"}
            if(categoryVal == "restaurant") {binding.chooseCategoryButton1.text="restauracje"}
            if(categoryVal == "gift") {binding.chooseCategoryButton1.text="prezenty"}
        }

        arguments?.getString(ARG_NOTES)?.let { selectedNotes->
            binding.noteEditText1.text=  Editable.Factory.getInstance().newEditable(selectedNotes.toString())
        }




        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

            if (selectedAmount != 0.0) {
                val formattedAmount = String.format("%.2f", selectedAmount)
                binding.amount1.text = Editable.Factory.getInstance().newEditable(formattedAmount)
            }
        }

        binding.undo1.setOnClickListener{
            val home = HomeFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, home)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        binding.chooseCalendarButton1.setOnClickListener {

            val amountString = binding.amount1.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0
            val calendarIncomesFragment = CalendarIncomesFragment.newInstance("val1",categoryVal, binding.noteEditText1.text.toString() ,amount)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarIncomesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.today1.setOnClickListener{
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH) + 1
            val year = currentDate.get(Calendar.YEAR)

            binding.selectDateEnd1.text = String.format("%02d.%02d.%d", day, month, year)

        }

        binding.yesterday1.setOnClickListener{
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_MONTH, -1)
            val dayYesterday = yesterday.get(Calendar.DAY_OF_MONTH)
            val month = yesterday.get(Calendar.MONTH) + 1
            val year = yesterday.get(Calendar.YEAR)

            binding.selectDateEnd1.text = String.format("%02d.%02d.%d", dayYesterday, month, year)

        }

        binding.chooseCategoryButton1.setOnClickListener{

            val amountString = binding.amount1.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0

            val categoryFragment = CategorySelectIncomeFragment.newInstance(binding.selectDateEnd1.text.toString(),"val2",binding.noteEditText1.text.toString(),amount)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, categoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        val budgetRef = db.collection(userId).document("budget")

        binding.confirmButtonIncomes1.setOnClickListener {
            val amount = binding.amount1.text.toString().toDoubleOrNull()
            val date = binding.selectDateEnd1.text.toString()
            val category = categoryVal
            val notes = binding.noteEditText1.text.toString()

            val budgetRef = db.collection(userId).document("budget")

            if( amount != null && date.isNotEmpty() && category.isNotEmpty() && notes.isNotEmpty()) {
                val incomeMap = hashMapOf(
                    "amount" to amount,
                    "date" to date,
                    "notes" to notes
                )
                budgetRef.collection(category).document().set(incomeMap).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Twój przychód został dodany", Toast.LENGTH_SHORT).show()


                    budgetRef.get().addOnSuccessListener { document ->
                        val currentBudget = document.getDouble("budgetV") ?: 0.0
                        val updatedBudget = currentBudget + amount

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
                    Toast.makeText(requireContext(), "Wystąpił błąd podczas dodawania przychodu", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }

        val view=binding.root
        return view

    }

    companion object {

        fun newInstance(selectedDate: String?=null, selectedCategory:String?=null, selectedNotes:String?=null, selectedAmount:Double?=0.0) =
            IncomesAddingFragment().apply {
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