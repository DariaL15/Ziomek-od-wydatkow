package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.budgetbuddy.databinding.FragmentIncomesAddingBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.*

private const val ARG_SELECTED_DATE = "selected_date"
private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
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
    ): View {


        binding = FragmentIncomesAddingBinding.inflate(inflater, container, false)

        binding.addExpensesButton1.setOnClickListener {
            val expensesAddingFragment = ExpensesAddingFragment.newInstance(null, 0, null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, expensesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
            binding.selectDateEnd1.text = selectedDate
        }

        val categorySpinner = binding.categorySpinner


        val categoryTranslations = mapOf(
            "choose" to "Wybierz",
            "car" to "Transport",
            "house" to "Dom",
            "clothes" to "Ubrania",
            "shopping" to "Zakupy",
            "transport" to "Inne",
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
            "sport", "health", "entertainment", "relax", "restaurant",
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
            binding.noteEditText1.text = Editable.Factory.getInstance().newEditable(selectedNotes)
        }


        val dotLocale = Locale("en", "US")

        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

            if (selectedAmount != 0.0) {
                val formattedAmount = String.format(dotLocale,"%.2f", selectedAmount)
                binding.amount1.text = Editable.Factory.getInstance().newEditable(formattedAmount)
            }
        }


        binding.chooseCalendarButton1.setOnClickListener {

            val amountString = binding.amount1.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0
            val calendarIncomesFragment = CalendarIncomesFragment.newInstance(
                selectedCategoryPosition,
                binding.noteEditText1.text.toString(),
                amount
            )
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarIncomesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.today1.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH) + 1
            val year = currentDate.get(Calendar.YEAR)

            binding.selectDateEnd1.text = String.format("%02d.%02d.%d", day, month, year)

        }

        binding.yesterday1.setOnClickListener {
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_MONTH, -1)
            val dayYesterday = yesterday.get(Calendar.DAY_OF_MONTH)
            val month = yesterday.get(Calendar.MONTH) + 1
            val year = yesterday.get(Calendar.YEAR)

            binding.selectDateEnd1.text = String.format("%02d.%02d.%d", dayYesterday, month, year)

        }


        val budgetRef = db.collection(userId).document("budget")

        binding.confirmButtonIncomes1.setOnClickListener {
            val amount = binding.amount1.text.toString().toDoubleOrNull()
            val date = binding.selectDateEnd1.text.toString()
            val category = selectedCategoryV
            val notes = binding.noteEditText1.text.toString()

            val budgetRef = db.collection(userId).document("budget")

            if (amount != null && date.isNotEmpty() && category.isNotEmpty() && notes.isNotEmpty() && category != "choose") {
                val incomeMap = hashMapOf(
                    "amount" to amount,
                    "date" to date,
                    "notes" to notes
                )
                budgetRef.collection(category).document().set(incomeMap).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Twój przychód został dodany",
                        Toast.LENGTH_SHORT
                    ).show()


                    budgetRef.get().addOnSuccessListener { document ->
                        val currentBudget = document.getDouble("budgetV") ?: 0.0
                        val updatedBudget = currentBudget + amount

                        budgetRef.update("budgetV", updatedBudget).addOnSuccessListener {
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
                        "Wystąpił błąd podczas dodawania przychodu",
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbarMenu = activity?.findViewById<Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<Toolbar>(R.id.toolbar_back)
        toolbarMenu?.visibility = View.GONE
        toolbarBack?.visibility = View.VISIBLE

        val textViewName = toolbarBack?.findViewById<TextView>(R.id.nameofpageback)
        textViewName?.text = "Dodaj tranzakcję"

        toolbarBack?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.textgreen))
        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)

        imageViewBack?.setOnClickListener(null)
        imageViewBack?.setOnClickListener {
            val fragmentBack = HomeFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragmentBack)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun onResume(){
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

    }

    override fun onStop(){
        super.onStop()
        (activity as AppCompatActivity?)?.supportActionBar?.show()
    }
    companion object {

        fun newInstance(selectedDate: String?=null, selectedCategoryPosition: Int? = 0, selectedNotes:String?=null, selectedAmount:Double?=0.0) =
            IncomesAddingFragment().apply {
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