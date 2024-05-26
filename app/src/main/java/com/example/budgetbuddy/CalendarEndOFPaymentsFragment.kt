package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.budgetbuddy.databinding.FragmentCalendarEndOFPaymentsBinding
import com.example.budgetbuddy.databinding.FragmentCalendarFixedExpensesBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.*
private const val ARG_NAME = "selected_name"
private const val ARG_AMOUNT = "selected_amount"
private const val ARG_SELECTED_DATE = "selected_date"
private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
private const val ARG_SELECTED_REPEAT_POSITION = "selected_repeat_position"
private const val ARG_SELECTED_END_PAYMENT_POSITION = "selected_end_payment_position"
private const val ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION = "selected_amount_end_payment_position"


class CalendarEndOFPaymentsFragment : Fragment() {

    private lateinit var binding: FragmentCalendarEndOFPaymentsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarEndOFPaymentsBinding.inflate(inflater, container, false)

        val selectedDateText:TextView=binding.selectDateText3
        val calendarView : CalendarView =binding.calendarView3
        val confirmButton : Button =binding.confirmButtonFix3
        var nameV= ""
        var amountV=0.0
        var dateBegin=""
        val categoryV = arguments?.getInt(ARG_SELECTED_CATEGORY_POSITION, 0) ?: 0
        val repeatV = arguments?.getInt(ARG_SELECTED_REPEAT_POSITION, 0) ?: 0
        val endPaymentV = arguments?.getInt(ARG_SELECTED_END_PAYMENT_POSITION, 0) ?: 0
        var amountDaysV =0




        setCurrentDate(selectedDateText)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth, selectedDateText)
        }

        arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
            dateBegin = selectedDate
        }

        arguments?.getString(ARG_NAME)?.let { selectedName ->
            nameV = selectedName
        }

        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->
            amountV = selectedAmount
        }

        arguments?.getInt(ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION)?.let { selectedDateEndPaymentPosition ->
            amountDaysV = selectedDateEndPaymentPosition
        }


        confirmButton.setOnClickListener {



            val selectedDate = selectedDateText.text.toString()
            val fef = FixedExpenseFragment.newInstance(nameV, amountV,  dateBegin, categoryV, repeatV, endPaymentV ,amountDaysV,selectedDate)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fef)
            transaction.addToBackStack(null)
            transaction.commit()
        }

            return binding.root
    }

    companion object {

        fun newInstance(selectedName: String, selectedAmount: Double,selectedDate: String, selectedCategoryPosition: Int, selectedRepeatPosition: Int, selectedEndPaymentPosition: Int,selectedAmountEndPaymentPosition: Int) =
            CalendarEndOFPaymentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME,selectedName)
                    putDouble(ARG_AMOUNT,selectedAmount)
                    putString(ARG_SELECTED_DATE, selectedDate)
                    putInt(ARG_SELECTED_CATEGORY_POSITION, selectedCategoryPosition)
                    putInt(ARG_SELECTED_REPEAT_POSITION, selectedRepeatPosition)
                    putInt(ARG_SELECTED_END_PAYMENT_POSITION, selectedEndPaymentPosition)
                    putInt(ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION, selectedAmountEndPaymentPosition)

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbarMenu = activity?.findViewById<Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<Toolbar>(R.id.toolbar_back)
        toolbarMenu?.visibility = View.GONE
        toolbarBack?.visibility = View.VISIBLE

        val textViewName = toolbarBack?.findViewById<TextView>(R.id.nameofpageback)
        textViewName?.text = "Wybierz dzień"


        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)

        imageViewBack?.setOnClickListener(null)
        imageViewBack?.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
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