package com.example.budgetbuddy

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.databinding.FragmentFixedExpenseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.*

private const val ARG_NAME = "selected_name"
private const val ARG_AMOUNT = "selected_amount"
private const val ARG_SELECTED_DATE = "selected_date"
private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
private const val ARG_SELECTED_REPEAT_POSITION = "selected_repeat_position"
private const val ARG_SELECTED_END_PAYMENT_POSITION = "selected_end_payment_position"
private const val ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION = "selected_amount_end_payment_position"
private const val ARG_SELECTED_DATE_END_PAYMENT_POSITION = "selected_date_end_payment_position"

class FixedExpenseFragment : Fragment() {

    private lateinit var binding: FragmentFixedExpenseBinding
    private var db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val repeat = listOf("codziennie","co tydzień","co dwa tygodnie", "co miesiąc" , "co trzy miesiące", "co sześć miesięcy", "co rok")
    private val end_payment= listOf("bezterminowo", "po liczbie przelewów", "w dniu")
            override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

                binding = FragmentFixedExpenseBinding.inflate(inflater, container, false)


                val repaetSpiner=binding.repeatSpinner
                val endPaymentSpiner=binding.endPaymentSpinner

                val adapterR = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, repeat)
                val adapterE = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, end_payment)

                adapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                adapterE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                repaetSpiner.adapter = adapterR
                endPaymentSpiner.adapter=adapterE

                var selectedRepeatSpinerPosition = 0
                var selectedEndPaymentSpinerPosition = 0

                arguments?.getString(ARG_NAME)?.let { selectedName ->
                        binding.fixedExpenseName.text = Editable.Factory.getInstance().newEditable(selectedName)
                }
                val dotLocale = Locale("en", "US")
                arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

                    if (selectedAmount != 0.0) {
                        val formattedAmount = String.format(dotLocale,"%.2f", selectedAmount)
                        binding.fixedExpenseEnterAmount.text = Editable.Factory.getInstance().newEditable(formattedAmount)
                    }
                }

                arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
                    binding.dateOfPayment.text = selectedDate
                }

                arguments?.getInt(ARG_SELECTED_REPEAT_POSITION)?.let { position ->
                    selectedRepeatSpinerPosition = position
                    repaetSpiner.setSelection(selectedRepeatSpinerPosition)
                }

                arguments?.getInt(ARG_SELECTED_END_PAYMENT_POSITION)?.let { position ->
                    selectedEndPaymentSpinerPosition = position
                    endPaymentSpiner.setSelection(selectedEndPaymentSpinerPosition)
                }

                arguments?.getString(ARG_SELECTED_DATE_END_PAYMENT_POSITION)?.let { selectedDateEnd ->
                    binding.dateOfEndOfPayment.text = selectedDateEnd


                }

                arguments?.getInt(ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION)?.let { selectedDayAmount ->
                    if(selectedDayAmount !=0 )
                    {
                        binding.numberOfTransfersEdit.setText(selectedDayAmount.toString())
                    }

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

                var selectedEndPayment = ""
                endPaymentSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            selectedEndPayment = end_payment[position]
                            selectedEndPaymentSpinerPosition=position
                            endPaymentSpiner.setSelection(selectedEndPaymentSpinerPosition)

                            when (selectedEndPayment) {
                                "po liczbie przelewów" -> {binding.amountOfPayments.visibility = View.VISIBLE
                                binding.endOfPayment.visibility = View.GONE}
                                "w dniu" -> {binding.endOfPayment.visibility = View.VISIBLE
                                binding.amountOfPayments.visibility = View.GONE}
                                else -> {
                                    binding.amountOfPayments.visibility = View.GONE
                                    binding.endOfPayment.visibility = View.GONE
                                }
                            }
                        }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                Toast.makeText(requireContext(), "Wybierz kiedy zakończyć zlecenie", Toast.LENGTH_SHORT).show()
                            }
                        }

                val categorySpinner = binding.selectCategoryFixedExpensesSpinner

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
                    val nameV = binding.fixedExpenseName.text.toString()
                    val amountString = binding.fixedExpenseEnterAmount.text.toString()
                    val amount = amountString.toDoubleOrNull() ?: 0.0
                    val daysAmountString = binding.numberOfTransfersEdit.text.toString()
                    val daysAmount = if (daysAmountString.isNotEmpty()) {
                        daysAmountString.toInt()
                    } else {
                        0
                    }
                    val dateEnd = binding.dateOfEndOfPayment.text.toString()


                    val calendarFixedExpensesFragment = CalendarFixedExpensesFragment.newInstance(
                        nameV,
                        amount,
                        selectedCategoryPosition,
                        selectedRepeatSpinerPosition,
                        selectedEndPaymentSpinerPosition,
                        daysAmount,
                        dateEnd

                    )
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, calendarFixedExpensesFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }


                binding.chooseCalendarButton2.setOnClickListener {
                    val nameV = binding.fixedExpenseName.text.toString()
                    val amountString = binding.fixedExpenseEnterAmount.text.toString()
                    val amount = amountString.toDoubleOrNull() ?: 0.0
                    val daysAmountString = binding.numberOfTransfersEdit.text.toString()
                    val daysAmount = if (daysAmountString.isNotEmpty()) {
                        daysAmountString.toInt()
                    } else {
                        0
                    }
                    val dateBegin= binding.dateOfPayment.text.toString()


                    val calendarEndOFFixedExpensesFragment = CalendarEndOFPaymentsFragment.newInstance(
                        nameV,
                        amount,
                        dateBegin,
                        selectedCategoryPosition,
                        selectedRepeatSpinerPosition,
                        selectedEndPaymentSpinerPosition,
                        daysAmount

                    )
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, calendarEndOFFixedExpensesFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                val budgetRef = db.collection(userId).document("fixedExpenses")

binding.confirmButtonFix1.setOnClickListener {
    val name=binding.fixedExpenseName.text.toString()
    val amount=binding.fixedExpenseEnterAmount.text.toString().toDoubleOrNull()
    val category = selectedCategoryV
    val dateBegin =binding.dateOfPayment.text.toString()
    val repeatValue = selectedRepeat
    val endDayRepeat = binding.dateOfEndOfPayment.text.toString()
    val daysAmountString = binding.numberOfTransfersEdit.text.toString()
    val whenStopFixedExpense = selectedEndPayment
    val amountOfTransfers = if (daysAmountString.isNotEmpty()) {
        daysAmountString.toInt()
    } else {
        0
    }


    if( name.isNotEmpty() && amount!=null && category.isNotEmpty() && dateBegin.isNotEmpty() && repeatValue.isNotEmpty() )
    {
        val fixedExpenseMap = hashMapOf(
            "name" to name,
            "amount" to amount,
            "category" to category,
            "beginDate" to dateBegin,
            "nextDate" to dateBegin,
            "repeatFrequency" to repeatValue,
            "endDayOfTransfers" to endDayRepeat,
            "amountOfTransfers" to amountOfTransfers,
            "amountOfTransfersTemp" to amountOfTransfers,
            "whenStopFixedExpense" to whenStopFixedExpense)
        budgetRef.collection("documents").document().set(fixedExpenseMap).addOnSuccessListener {
            Toast.makeText(context,"Zlecenie stałe dodane",Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(context,"Zlecenie stałe nie dodane",Toast.LENGTH_SHORT).show()
            }
        val home = ListOfFixedExpensesFragment.newInstance()
        val transaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, home)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    else
    {
        Toast.makeText(context, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
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
        textViewName?.text = "Dodaj zlecenie stałe"

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

    companion object {

        fun newInstance(selectedName: String?=null, selectedAmount: Double?=0.0, selectedDate:String?=null,selectedCategoryPosition: Int? = 0, selectedRepeatPosition: Int? = 0, selectedEndPaymentPosition: Int? = 0,selectedAmountEndPaymentPosition: Int? = 0, selectedDateEndPaymentPosition:String?=null) =
            FixedExpenseFragment().apply {
                arguments = Bundle().apply {

                    if (selectedName != null ) {
                        putString(ARG_NAME, selectedName)
                    }
                    if (selectedDate != null ) {
                        putString(ARG_SELECTED_DATE, selectedDate)
                    }
                    if (selectedAmount != null ) {
                        putDouble(ARG_AMOUNT, selectedAmount)
                    }

                    if (selectedCategoryPosition != null) {
                        putInt(ARG_SELECTED_CATEGORY_POSITION, selectedCategoryPosition)
                    }

                    if (selectedRepeatPosition != null) {
                        putInt(ARG_SELECTED_REPEAT_POSITION, selectedRepeatPosition)
                    }

                    if (selectedEndPaymentPosition != null) {
                        putInt(ARG_SELECTED_END_PAYMENT_POSITION, selectedEndPaymentPosition)
                    }

                    if (selectedAmountEndPaymentPosition != null) {
                        putInt(ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION, selectedAmountEndPaymentPosition)
                    }
                    if (selectedDateEndPaymentPosition != null ) {
                        putString(ARG_SELECTED_DATE_END_PAYMENT_POSITION, selectedDateEndPaymentPosition)
                    }

                }
            }
    }

}