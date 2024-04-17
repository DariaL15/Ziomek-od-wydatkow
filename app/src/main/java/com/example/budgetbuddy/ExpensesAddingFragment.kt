package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.budgetbuddy.databinding.FragmentExpensesAddingBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


private const val ARG_SELECTED_DATE = "selected_date"
private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
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
    ): View {


        binding = FragmentExpensesAddingBinding.inflate(inflater, container, false)

        binding.addIncomeButton.setOnClickListener {
            val incomesAddingFragment = IncomesAddingFragment.newInstance(null, null, null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, incomesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
            binding.selectDateEnd.text = selectedDate
        }


        val categorySpinner = binding.categorySpinner


        val categoryTranslations = mapOf(
            "choose" to "Wybierz",
            "car" to "Samochód",
            "house" to "Dom",
            "clothes" to "Ubrania",
            "shopping" to "Zakupy",
            "transport" to "Transport",
            "sport" to "Sport",
            "health" to "Zdrowie",
            "entertainment" to "Rozrywka",
            "relax" to "Relax",
            "restaurant" to "Restauracje",
            "gift" to "Prezenty",
            "education" to "Edukacja"
        )

        val categoriesEnglish = listOf(
            "choose", "car", "house", "clothes", "shopping", "transport",
            "sport", "health", "entertaiment", "relax", "restaurant",
            "gift", "education"
        )

        val categoriesPolish = categoriesEnglish.map { categoryTranslations[it] ?: it }


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoriesPolish
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        var selectedCategoryV = ""
        var selectedCategoryPosition = 0


        arguments?.getInt(ARG_SELECTED_CATEGORY_POSITION)?.let { position ->
            selectedCategoryPosition = position
            categorySpinner.setSelection(selectedCategoryPosition)
        }

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategoryPosition = position
                selectedCategoryV = categoriesEnglish[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Wybierz kategorie", Toast.LENGTH_SHORT).show()
            }
        }

        arguments?.getString(ARG_NOTES)?.let { selectedNotes ->
            binding.noteEditText.text = Factory.getInstance().newEditable(selectedNotes)
        }




        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

            if (selectedAmount != 0.0) {
                val formattedAmount = String.format("%.2f", selectedAmount)
                binding.amount.text = Factory.getInstance().newEditable(formattedAmount)
            }
        }

        binding.undo.setOnClickListener {
            val home = HomeFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, home)
            transaction.addToBackStack(null)
            transaction.commit()

        }



        binding.chooseCalendarButton.setOnClickListener {

            val amountString = binding.amount.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0

            val calendarFragment = CalendarFragment.newInstance(
                selectedCategoryPosition,
                binding.noteEditText.text.toString(),
                amount
            )
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.today.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH) + 1
            val year = currentDate.get(Calendar.YEAR)

            binding.selectDateEnd.text = String.format("%02d.%02d.%d", day, month, year)

        }

        binding.yesterday.setOnClickListener {
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_MONTH, -1)
            val dayYesterday = yesterday.get(Calendar.DAY_OF_MONTH)
            val month = yesterday.get(Calendar.MONTH) + 1
            val year = yesterday.get(Calendar.YEAR)

            binding.selectDateEnd.text = String.format("%02d.%02d.%d", dayYesterday, month, year)

        }


        val budgetRef = db.collection(userId).document("budget")

        binding.confirmButton2.setOnClickListener {
            val amount = binding.amount.text.toString().toDoubleOrNull()
            val date = binding.selectDateEnd.text.toString()
            val category = selectedCategoryV
            val notes = binding.noteEditText.text.toString()

            val budgetRef = db.collection(userId).document("budget")

            if (amount != null && date.isNotEmpty() && category.isNotEmpty() && notes.isNotEmpty() && category != "choose") {
                val expenseMap = hashMapOf(
                    "amount" to -amount,
                    "date" to date,
                    "notes" to notes
                )
                budgetRef.collection(category).document().set(expenseMap).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Twój wydatek został dodany",
                        Toast.LENGTH_SHORT
                    ).show()


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
                            Toast.makeText(
                                requireContext(),
                                "Budżet został zaktualizowany",
                                Toast.LENGTH_SHORT
                            ).show()

                            val home = HomeFragment.newInstance()
                            val transaction =
                                requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, home)
                            transaction.addToBackStack(null)
                            transaction.commit()
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
            } else {
                Toast.makeText(requireContext(), "Wypełnij wszystkie pola", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        return binding.root
    }

    companion object {

        fun newInstance(selectedDate: String?=null, selectedCategoryPosition: Int? = 0, selectedNotes:String?=null, selectedAmount:Double?=0.0 ) =
            ExpensesAddingFragment().apply {
                arguments = Bundle().apply {

                    if (selectedDate != null ) {
                        putString(ARG_SELECTED_DATE, selectedDate)
                    }
                    if (selectedCategoryPosition != null) {
                        putInt(ARG_SELECTED_CATEGORY_POSITION, selectedCategoryPosition)
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