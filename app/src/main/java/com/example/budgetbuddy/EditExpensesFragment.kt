package com.example.budgetbuddy

import android.content.ContentValues
import android.content.ContentValues.TAG
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
import androidx.core.content.ContextCompat
import com.example.budgetbuddy.databinding.FragmentEditExpensesBinding
import com.example.budgetbuddy.databinding.FragmentEditFixedExpensesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore


class EditExpensesFragment : Fragment() {

    private lateinit var binding: FragmentEditExpensesBinding
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var db = Firebase.firestore



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
       binding = FragmentEditExpensesBinding.inflate(inflater, container, false)

        val documentId = arguments?.getString("documentId")
        val originalCollection = arguments?.getString("collection")

        val args = arguments
        if (args != null) {
            val notes = args.getString("notes" )
            val amount = args.getDouble("amount", 0.0)
            val date = args.getString("date")

            binding.noteEditText1.setText(notes)
            binding.amount1.setText(amount.toString())
            binding.selectDateEnd1.text=date
        }

        val categorySpinner = binding.categorySpinner1
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


        arguments?.getInt(ARG_SELECTED_CATEGORY_POSITION)?.let { position ->
            selectedCategoryPosition = position
            categorySpinner.setSelection(selectedCategoryPosition)
        }
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



        binding.chooseCalendarButton1.setOnClickListener {

            val amountString = binding.amount1.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0


            val calendarFragment = CalendarEditExpensesFragment.newInstance()
            val args = Bundle()
            args.putString("documentId", documentId)
            args.putString("notes", binding.noteEditText1.text.toString())
            args.putDouble("amount", amount)
            args.putString("date", binding.selectDateEnd1.text.toString())
            args.putString("collection",selectedCategoryV)
            args.putInt("categoryPos", selectedCategoryPosition)
            args.putString("oldCollection",originalCollection.toString() )
            calendarFragment.arguments = args
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
            binding.selectDateEnd1.text = selectedDate
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






        binding.confirmButton21.setOnClickListener{
            val notes = binding.noteEditText1.text.toString()
            val amount = binding.amount1.text.toString().toDoubleOrNull() ?:0.0
            val date = binding.selectDateEnd1.text.toString()



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

        toolbarMenu?.visibility = View.GONE
        toolbarBack?.visibility = View.VISIBLE

        toolbarBack?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.redlight))

        val textViewName = toolbarBack?.findViewById<TextView>(R.id.nameofpageback)
        textViewName?.text = "Edytuj swoje wypłaty"

        val orgColl = arguments?.getString("collection")

        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)
        imageViewBack?.setOnClickListener(null)
        imageViewBack?.setOnClickListener {

            val fragmentBack = when(orgColl) {
                "car"->CarHistoryFragment.newInstance()
                "house"->HomeHistoryFragment.newInstance()
                "health"->HealthHistoryFragment.newInstance()
                "sport"->SportHistoryFragment.newInstance()
                "shopping"->ZakupyHistoryFragment.newInstance()
                "transport"->TransportHistoryFragment.newInstance()
                "relax"->WypoczynekHistoryFragment.newInstance()
                "clothes"->UbranieHistoryFragment.newInstance()
                "entertainment"->RozrywkaHistoryFragment.newInstance()
                "restaurant"->RestauracjaHistoryFragment.newInstance()
                "gift"->PrezentHistoryFragment.newInstance()
                "education"->EditIncomesFragment.newInstance()
                else -> HomeFragment.newInstance()
            }
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
        val budgetRef = db.collection(userId).document("budget")
        budgetRef.get().addOnSuccessListener { document->
            val  currentBudget = document.getDouble("budgetV")?:0.0
            val currentExpenses = document.getDouble("expensesV") ?: 0.0
            val currentAmount = arguments?.getDouble("amount", 0.0)
            val updateAmount = binding.amount1.text.toString().toDoubleOrNull()
            val updatedBudget : Double
            val updatedExpenses: Double
            if (currentAmount!! > updateAmount!!){
                updatedBudget = currentBudget + (currentAmount - updateAmount)
                updatedExpenses = currentExpenses - (currentAmount - updateAmount)
            }
            else{
                updatedBudget = currentBudget - ( updateAmount - currentAmount)
                updatedExpenses = currentExpenses + ( updateAmount -currentAmount )
            }
            budgetRef.update(
                mapOf(
                    "budgetV" to updatedBudget,
                    "expensesV" to updatedExpenses
                )
            ). addOnSuccessListener {
                Log.w(TAG, "Budżet został zaktualizowany")
            }

        }

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
                    Log.d(TAG, "Zmiany zostały zapisane")
                }
                .addOnFailureListener{e->
                    Log.w(TAG, "Zmiany nie zostały zapisane",e)
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
        fun newInstance(

        ) =
            EditExpensesFragment().apply {
                arguments = Bundle().apply {

                    }
                }
            }
    }




