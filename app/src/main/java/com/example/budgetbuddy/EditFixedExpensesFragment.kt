package com.example.budgetbuddy

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.budgetbuddy.databinding.FragmentEditFixedExpensesBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class EditFixedExpensesFragment : Fragment() {

    private lateinit var binding: FragmentEditFixedExpensesBinding
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var db = Firebase.firestore
    private val repeat = listOf("codziennie","co tydzień","co dwa tygodnie", "co miesiąc" , "co trzy miesiące", "co sześć miesięcy", "co rok")
    private val end_payment= listOf("bezterminowo", "po liczbie przelewów", "w dniu")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditFixedExpensesBinding.inflate(inflater, container, false)

        val endDayOfTransfers=  arguments?.getString("endDayOfTransfers")
        val beginDate =  arguments?.getString("beginDate")

        binding.dateOfPayment1.text = beginDate
        binding.dateOfEndOfPayment1.text = endDayOfTransfers


        val repaetSpiner=binding.repeatSpinner1
        val endPaymentSpiner=binding.endPaymentSpinner1

        val repeatFrequency = arguments?.getString("repeatFrequency")
        val whenStopPayment = arguments?.getString("whenStopFixedExpense")

        val adapterR = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, repeat)
        val adapterE = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, end_payment)

        adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        repaetSpiner.adapter = adapterR
        endPaymentSpiner.adapter=adapterE

        repaetSpiner.setSelection(getPositionForRepeatFrequency(repeatFrequency))
        endPaymentSpiner.setSelection(getPositionForWhenStop(whenStopPayment))
        var selectedRepeatSpinerPosition = 0
        var selectedEndPaymentSpinerPosition = 0

        arguments?.getString(ARG_NAME)?.let { selectedName ->
            binding.fixedExpenseName1.text = Editable.Factory.getInstance().newEditable(selectedName)
        }

        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

            if (selectedAmount != 0.0) {
                val formattedAmount = String.format("%.2f", selectedAmount)
                binding.fixedExpenseEnterAmount1.text = Editable.Factory.getInstance().newEditable(formattedAmount)
            }
        }

        arguments?.getInt(ARG_AMOUNT_OF_TRANSFERS)?.let { selectedDayAmount ->
            if(selectedDayAmount !=0 )
            {
                binding.numberOfTransfersEdit1.setText(selectedDayAmount.toString())
            }
        }

        arguments?.getString(ARG_BEGIN_DATE)?.let { selectedDate ->
            binding.dateOfPayment1.text = selectedDate
        }

        arguments?.getString(ARG_END_DAY_OF_TRANSFERS)?.let { selectedDate ->
            binding.dateOfEndOfPayment1.text = selectedDate
        }

        arguments?.getInt(ARG_AMOUNT_OF_TRANSFERS)?.let{ selectedAmountOfTransfers ->
            if (selectedAmountOfTransfers!=0)
                binding.numberOfTransfersEdit1.setText(selectedAmountOfTransfers.toString())
        }


        var selectedRepeat=""
        repaetSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRepeat = repeat[position]
                selectedRepeatSpinerPosition=position
                repaetSpiner.setSelection(selectedRepeatSpinerPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Wybierz co jaki czas powtarzać zlecenie", Toast.LENGTH_SHORT).show()
            }
        }

        var selectedEndPayment=""
        endPaymentSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEndPayment = end_payment[position]
                selectedEndPaymentSpinerPosition=position
                endPaymentSpiner.setSelection(selectedEndPaymentSpinerPosition)

                when (selectedEndPayment) {
                    "po liczbie przelewów" -> {binding.amountOfPayments1.visibility = View.VISIBLE
                        binding.endOfPayment1.visibility = View.GONE}
                    "w dniu" -> {binding.endOfPayment1.visibility = View.VISIBLE
                        binding.amountOfPayments1.visibility = View.GONE}
                    else -> {
                        binding.amountOfPayments1.visibility = View.GONE
                        binding.endOfPayment1.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Wybierz kiedy zakończyć zlecenie", Toast.LENGTH_SHORT).show()
            }
        }


        val categorySpinner = binding.selectCategoryFixedExpensesSpinner1
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

        arguments?.getString("category")?.let{collection->
            selectedCategoryV = collection
            selectedCategoryPosition = categoriesEnglish.indexOf(collection)
        }

        categorySpinner.setSelection(selectedCategoryPosition)//54546445

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

        binding.chooseCalendarButton11.setOnClickListener {

            val amountString = binding.fixedExpenseEnterAmount1.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0
            val nameV = binding.fixedExpenseName1.text.toString()
            val daysAmountString = binding.numberOfTransfersEdit1.text.toString()
            val daysAmount = if (daysAmountString.isNotEmpty()) {
                daysAmountString.toInt()
            } else {
                0
            }
            val dateEnd = binding.dateOfEndOfPayment1.text.toString()

            val calendarFragment = CalendarEditFixedExpensesFragment.newInstance(
                nameV,
                amount,
                selectedCategoryPosition,
                selectedRepeatSpinerPosition,
                selectedEndPaymentSpinerPosition,
                daysAmount,
                dateEnd
            )
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.chooseCalendarButton21.setOnClickListener {
            val nameV = binding.fixedExpenseName1.text.toString()
            val amountString = binding.fixedExpenseEnterAmount1.text.toString()
            val amount = amountString.toDoubleOrNull() ?: 0.0
            val daysAmountString = binding.numberOfTransfersEdit1.text.toString()
            val daysAmount = if (daysAmountString.isNotEmpty()) {
                daysAmountString.toInt()
            } else {
                0
            }
            val dateBegin= binding.dateOfPayment1.text.toString()


            val calendarEditEndOFFixedExpensesFragment = CalendarEndOFPaymentsFragment.newInstance(
                nameV,
                amount,
                dateBegin,
                selectedCategoryPosition,
                selectedRepeatSpinerPosition,
                selectedEndPaymentSpinerPosition,
                daysAmount

            )
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, calendarEditEndOFFixedExpensesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }





        binding.confirmButtonFix11.setOnClickListener{
            val name = binding.fixedExpenseName1.text.toString()
            val amount = binding.fixedExpenseEnterAmount1.text.toString().toDoubleOrNull() ?:0.0
            val category = selectedCategoryV
            val dateBegin =binding.dateOfPayment1.text.toString()
            val repeatValue = selectedRepeat
            val dateNext = arguments?.getString("nextDate").toString()//Потом заменитьб посчитать новую дату
            val endDayRepeat = binding.dateOfEndOfPayment1.text.toString()
            val whenStop = selectedEndPayment
            val daysAmountString = binding.numberOfTransfersEdit1.text.toString()//po liczbie przelewow
            val amountOfTransfers = if (daysAmountString.isNotEmpty()) {
                daysAmountString.toInt()
            } else {
                0
            }

            val args = arguments
            val documentId = args?.getString("documentId")


            if( documentId!=null  )
            {
                updateDocumentInDatabase(documentId, name, amount, category, dateBegin, dateNext, repeatValue, endDayRepeat, amountOfTransfers, whenStop )
            }

            val home = ListOfFixedExpensesFragment.newInstance()
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
        val toolbarMenu = activity?.findViewById<Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<Toolbar>(R.id.toolbar_back)
        toolbarMenu?.visibility = View.GONE
        toolbarBack?.visibility = View.VISIBLE

        val textViewName = toolbarBack?.findViewById<TextView>(R.id.nameofpageback)
        textViewName?.text = "Edytuj stałe zlecenie"

        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)
        toolbarBack?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.textgreen))

        imageViewBack?.setOnClickListener(null)
        imageViewBack?.setOnClickListener {
            val fragmentBack = ListOfFixedExpensesFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragmentBack)
                ?.addToBackStack(null)
                ?.commit()
        }


    }

    private fun getPositionForRepeatFrequency(repeatFrequency: String?): Int {
        return repeat.indexOf(repeatFrequency)
    }

    private fun getPositionForWhenStop(whenStopPayment: String?): Int {
        return repeat.indexOf(whenStopPayment)
    }

    private fun updateDocumentInDatabase(
        documentId: String,
        name: String,
        amount: Double,
        category: String,
        dateBegin: String,
        dateNext: String,
        repeatValue: String,
        endDayRepeat: String,
        amountOfTransfers: Int,
        whenStop: String
    ){
            db.collection(userId).document("fixedExpenses").collection("documents")
                .document(documentId)
                .update(
                    mapOf(
                        "name" to name,
                        "amount" to amount,
                        "beginDate" to   dateBegin,
                        "nextDate" to dateNext,
                        "category"   to category,
                        "repeatFrequency" to  repeatValue,
                        "amountOfTransfers" to amountOfTransfers,
                        "endDayOfTransfers" to endDayRepeat,
                        "amountOfTransfersTemp" to amountOfTransfers,
                        "whenStopFixedExpense" to whenStop
                    )
                )
                .addOnSuccessListener {
                    Log.d(TAG, "Zmiany zostały zapisane")
                }
                .addOnFailureListener{e->
                    Log.w(TAG, "Zmiany nie zostały zapisane",e)
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
        private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_category"
        private const val ARG_NAME = "name"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_REPEAT_FREQUENCY_POSITION = "repeatFrequencyPos"
        private const val ARG_BEGIN_DATE="beginDate"
        private const val ARG_NEXT_DATE="nextDate"//для вычисления
        private const val ARG_AMOUNT_OF_TRANSFERS="amountOfTransfers"
        private const val ARG_END_DAY_OF_TRANSFERS="endDayOfTransfers"
        private const val ARG_AMOUNT_OF_TRANSFERS_TEMP="amountOfTransfersTemp"//для вычесления
        private const val ARG_WHEN_STOP_FIXED_EXPENSE="whenStopExpense"
        fun newInstance(
            selectedDate: String?=null,
            selectedNextDate: String?=null,
            selectedCategoryPosition: Int? = 0,
            selectedName:String?=null,
            selectedAmount:Double?=0.0,
            selectedRepeatFrequency: Int?=0,
            selectedAmountOfTransfers: Int?=0,
            selectedEndDayOfTransfers:String?=null,
            selectedAmountOfTransfersTemp:Int?=0,
            selectedWhenStopFixedExpense: Int?=0
        ) =
            EditFixedExpensesFragment().apply {
                arguments = Bundle().apply {

                    if (selectedDate != null ) {
                        putString(ARG_BEGIN_DATE, selectedDate)
                    }
                    if (selectedDate != null ) {
                        putString(ARG_NEXT_DATE, selectedNextDate)
                    }
                    if (selectedCategoryPosition != null) {
                        putInt(ARG_SELECTED_CATEGORY_POSITION, selectedCategoryPosition)
                    }
                    if (selectedName != null ) {
                        putString(ARG_NAME, selectedName)
                    }
                    if (selectedAmount != null) {
                        putDouble(ARG_AMOUNT, selectedAmount)
                    }
                    if (selectedRepeatFrequency != null) {
                        putInt(ARG_REPEAT_FREQUENCY_POSITION, selectedRepeatFrequency)
                    }
                    if (selectedAmountOfTransfers != null) {
                    putInt(ARG_AMOUNT_OF_TRANSFERS, selectedAmountOfTransfers)
                    }
                    if (selectedAmountOfTransfersTemp != null) {
                    putInt(ARG_AMOUNT_OF_TRANSFERS_TEMP, selectedAmountOfTransfersTemp)
                    }
                    if (selectedEndDayOfTransfers!= null) {
                        putString(ARG_END_DAY_OF_TRANSFERS, selectedEndDayOfTransfers)
                    }
                    if(selectedWhenStopFixedExpense!=null)
                        putInt(ARG_WHEN_STOP_FIXED_EXPENSE, selectedWhenStopFixedExpense)
                }
            }
    }
}