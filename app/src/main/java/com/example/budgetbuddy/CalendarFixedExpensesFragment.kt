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
import com.example.budgetbuddy.databinding.FragmentCalendarFixedExpensesBinding
import com.example.budgetbuddy.databinding.FragmentCalendarIncomesBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.*

private const val ARG_NAME = "selected_name"
private const val ARG_AMOUNT = "selected_amount"
private const val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
private const val ARG_SELECTED_REPEAT_POSITION = "selected_repeat_position"
private const val ARG_SELECTED_END_PAYMENT_POSITION = "selected_end_payment_position"
private const val ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION = "selected_amount_end_payment_position"
private const val ARG_SELECTED_DATE_END_PAYMENT_POSITION = "selected_date_end_payment_position"


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
        val categoryV = arguments?.getInt(ARG_SELECTED_CATEGORY_POSITION, 0) ?: 0
        val repeatV = arguments?.getInt(ARG_SELECTED_REPEAT_POSITION, 0) ?: 0
        val endPaymentV = arguments?.getInt(ARG_SELECTED_END_PAYMENT_POSITION, 0) ?: 0
        var amountDaysV =0
        var dateEnd =" "


        setCurrentDate(selectedDateText)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth, selectedDateText)
        }
        arguments?.getString(ARG_SELECTED_DATE_END_PAYMENT_POSITION)?.let { selectedDate ->
            dateEnd = selectedDate
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
            val fef = FixedExpenseFragment.newInstance(nameV, amountV,  selectedDate, categoryV, repeatV, endPaymentV ,amountDaysV,dateEnd)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fef)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        return binding.root


    }

    companion object {

        fun newInstance(selectedName: String, selectedAmount: Double, selectedCategoryPosition: Int, selectedRepeatPosition: Int, selectedEndPaymentPosition: Int,selectedAmountEndPaymentPosition: Int,selectedDateEndPaymentPosition:String) =
            CalendarFixedExpensesFragment().apply {
                arguments = Bundle().apply {

                    putString(ARG_NAME,selectedName)
                    putDouble(ARG_AMOUNT,selectedAmount)
                    putInt(ARG_SELECTED_CATEGORY_POSITION, selectedCategoryPosition)
                    putInt(ARG_SELECTED_REPEAT_POSITION, selectedRepeatPosition)
                    putInt(ARG_SELECTED_END_PAYMENT_POSITION, selectedEndPaymentPosition)
                    putInt(ARG_SELECTED_AMOUNT_END_PAYMENT_POSITION, selectedAmountEndPaymentPosition)
                    putString(ARG_SELECTED_DATE_END_PAYMENT_POSITION, selectedDateEndPaymentPosition)

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

        toolbarBack?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.textgreen))

        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)

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