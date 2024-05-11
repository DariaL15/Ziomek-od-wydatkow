package com.example.budgetbuddy

import android.content.ContentValues
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import com.example.budgetbuddy.databinding.FragmentEditFixedExpensesBinding
import com.example.budgetbuddy.databinding.FragmentEditIncomesBinding
import com.example.budgetbuddy.databinding.FragmentIncomesAddingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class EditIncomesFragment : Fragment() {

    private lateinit var binding: FragmentEditIncomesBinding
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditIncomesBinding.inflate(inflater, container, false)

        val args = arguments
        if (args != null) {
            val date = args.getString("date")
            binding.selectDateEnd21.text=date
        }

        val categorySpinner = binding.categorySpinner21
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


        arguments?.getString("collection")?.let{collection->
            selectedCategoryV = collection
            selectedCategoryPosition = categoriesEnglish.indexOf(collection)
        }

        categorySpinner.setSelection(selectedCategoryPosition)

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


        binding.chooseCalendarButton221.setOnClickListener {

            val amountString = binding.amount31.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0

            val calendarFragment = CalendarEditIncomesFragment.newInstance(
                selectedCategoryPosition,
                binding.noteEditText21.text.toString(),
                amount
            )
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.today21.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH) + 1
            val year = currentDate.get(Calendar.YEAR)

            binding.selectDateEnd21.text = String.format("%02d.%02d.%d", day, month, year)

        }

        binding.yesterday21.setOnClickListener {
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_MONTH, -1)
            val dayYesterday = yesterday.get(Calendar.DAY_OF_MONTH)
            val month = yesterday.get(Calendar.MONTH) + 1
            val year = yesterday.get(Calendar.YEAR)

            binding.selectDateEnd21.text = String.format("%02d.%02d.%d", dayYesterday, month, year)

        }

        arguments?.getString(ARG_NOTES)?.let { selectedNotes ->
            binding.noteEditText21.text = Editable.Factory.getInstance().newEditable(selectedNotes)
        }

        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

            if (selectedAmount != 0.0) {
                val formattedAmount = String.format("%.2f", selectedAmount)
                binding.amount31.text = Editable.Factory.getInstance().newEditable(formattedAmount)
            }
        }



        arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
            binding.selectDateEnd21.text = selectedDate
        }


        binding.confirmButtonIncomes21.setOnClickListener{
            val notes = binding.noteEditText21.text.toString()
            val amount = binding.amount31.text.toString().toDoubleOrNull() ?:0.0
            val date = binding.selectDateEnd21.text.toString()

            val args = arguments
            val documentId = args?.getString("documentId")
            val originalCollection = args?.getString("collection")


            if( documentId!=null && originalCollection!=null )
            {
                updateDocumentInDatabase(documentId, notes, amount, date, originalCollection, selectedCategoryV)
            }

            val home = HomeFragment.newInstance()
            val transaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, home)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbarBack = activity?.findViewById<Toolbar>(R.id.toolbar_back)
        val toolbarMenu = activity?.findViewById<Toolbar>(R.id.toolbar)

        val textViewName = toolbarBack?.findViewById<TextView>(R.id.nameofpageback)
        textViewName?.text = "Edytuj swoje wpłaty"
        toolbarMenu?.visibility = View.GONE
        toolbarBack?.visibility = View.VISIBLE

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

    private fun updateDocumentInDatabase(
        documentId: String,
        notes: String,
        amount: Double,
        date: String,
        originalCollection: String,
        newCollection: String
    ){
        if(originalCollection==newCollection)
        {
            db.collection(userId).document("budget").collection(originalCollection)
                .document(documentId)
                .update(
                    mapOf(
                        "notes" to notes,
                        "amount" to amount,
                        "date" to date
                    )
                )
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Zmiany zostały zapisane")
                }
                .addOnFailureListener{e->
                    Log.w(ContentValues.TAG, "Zmiany nie zostały zapisane",e)
                }
        }else{
            val originalDocumentRef = db.collection(userId).document("budget").collection(originalCollection).document(documentId)
            val newDocumentRef = db.collection(userId).document("budget").collection(newCollection).document()
            db.runBatch{batch->
                batch.set(newDocumentRef, mapOf("notes" to notes, "amount" to amount, "date" to date))
                batch.delete(originalDocumentRef)
            }.addOnSuccessListener {
                Log.d(ContentValues.TAG,"Zmiany zostały zapisane" )
            }.addOnFailureListener{e->
                Log.w(ContentValues.TAG, "Zmiany nie zostały zapisane", e)
            }
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
        private const val ARG_SELECTED_DATE = "selected_date"
        private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
        private const val ARG_NOTES = "notes"
        private const val ARG_AMOUNT = "amount"
        fun newInstance(
            selectedDate: String?=null,
            selectedCategoryPosition: Int? = 0,
            selectedNotes:String?=null,
            selectedAmount:Double?=0.0
        ) =
            EditIncomesFragment().apply {
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