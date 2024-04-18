package com.example.budgetbuddy

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.budgetbuddy.databinding.FragmentFixedExpenseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


private const val ARG_NAME = "selected_name"
private const val ARG_AMOUNT = "selected_amount"
private const val ARG_SELECTED_DATE = "selected_date"
//private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
private const val ARG_SELECTED_REPEAT_POSITION = "selected_repeat_position"
private const val ARG_SELECTED_END_PAYMENT_POSITION = "selected_end_payment_position"

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

        binding.undo.setOnClickListener {
            val home = HomeFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, home)
            transaction.addToBackStack(null)
            transaction.commit()

        }
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

                arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->

                    if (selectedAmount != 0.0) {
                        val formattedAmount = String.format("%.2f", selectedAmount)
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

                repaetSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedRepeat = repeat[position]
                        selectedRepeatSpinerPosition=position
                        repaetSpiner.setSelection(selectedRepeatSpinerPosition)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        Toast.makeText(requireContext(), "Wybierz co jaki czas powtarzać zlecenie", Toast.LENGTH_SHORT).show()
                    }
                }


                        endPaymentSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedEndPayment = end_payment[position]
                            selectedEndPaymentSpinerPosition=position
                            endPaymentSpiner.setSelection(selectedEndPaymentSpinerPosition)
                        }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                Toast.makeText(requireContext(), "Wybierz kiedy zakończyć zlecenie", Toast.LENGTH_SHORT).show()
                            }
                        }


                binding.chooseCalendarButton1.setOnClickListener {
                    val nameV = binding.fixedExpenseName.text.toString()
                    val amountString = binding.fixedExpenseEnterAmount.text.toString()
                    val amount = amountString.toDoubleOrNull() ?: 0.0



                    val calendarFixedExpensesFragment = CalendarFixedExpensesFragment.newInstance(
                        nameV,
                        amount,
                        selectedRepeatSpinerPosition,
                        selectedEndPaymentSpinerPosition
                    )
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, calendarFixedExpensesFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

                return binding.root
    }

    companion object {

        fun newInstance(selectedName: String?=null, selectedAmount: Double?=0.0, selectedDate:String?=null, selectedRepeatPosition: Int? = 0, selectedEndPaymentPosition: Int? = 0) =
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

                    if (selectedRepeatPosition != null) {
                        putInt(ARG_SELECTED_REPEAT_POSITION, selectedRepeatPosition)
                    }

                    if (selectedEndPaymentPosition != null) {
                        putInt(ARG_SELECTED_END_PAYMENT_POSITION, selectedEndPaymentPosition)
                    }


                }
            }
    }
}