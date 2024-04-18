package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import com.example.budgetbuddy.databinding.FragmentCalendarFixedExpensesBinding
import com.example.budgetbuddy.databinding.FragmentCalendarIncomesBinding
import java.text.SimpleDateFormat
import java.util.Locale


private const val ARG_NAME = "selected_name"
private const val ARG_AMOUNT = "selected_amount"
//private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
private const val ARG_SELECTED_REPEAT_POSITION = "selected_repeat_position"
private const val ARG_SELECTED_END_PAYMENT_POSITION = "selected_end_payment_position"



class CalendarFixedExpensesFragment : Fragment() {

    private lateinit var binding: FragmentCalendarFixedExpensesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCalendarFixedExpensesBinding.inflate(inflater, container, false)
        val selectedDateText:TextView= binding.selectedDate2
        val calendarView :CalendarView=binding.calendarView2
        val confirmButton : Button= binding.confirmButtonFix2
        var nameV= ""
        var amountV=0.0
        val repeatV = arguments?.getInt(ARG_SELECTED_REPEAT_POSITION, 0) ?: 0
        val endPaymentV = arguments?.getInt(ARG_SELECTED_END_PAYMENT_POSITION, 0) ?: 0

        setCurrentDate(selectedDateText)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth, selectedDateText)
        }



        confirmButton.setOnClickListener {

            arguments?.getString(ARG_NAME)?.let { selectedName ->
                nameV = selectedName
            }

            arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->
                amountV = selectedAmount
            }

            val selectedDate = selectedDateText.text.toString()
            val fef = FixedExpenseFragment.newInstance(nameV, amountV, selectedDate, repeatV, endPaymentV )
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fef)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        return binding.root


    }

    companion object {

        fun newInstance(selectedName: String, selectedAmount: Double, selectedRepeatPosition: Int, selectedEndPaymentPosition: Int) =
            CalendarFixedExpensesFragment().apply {
                arguments = Bundle().apply {

                    putString(ARG_NAME,selectedName)
                    putDouble(ARG_AMOUNT,selectedAmount)
                    putInt(ARG_SELECTED_REPEAT_POSITION, selectedRepeatPosition)
                    putInt(ARG_SELECTED_END_PAYMENT_POSITION, selectedEndPaymentPosition)


                }
            }
    }

    private fun setCurrentDate(textView: TextView) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        textView.text = formattedDate
    }

    private fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int, textView: TextView) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        textView.text = formattedDate
    }
}