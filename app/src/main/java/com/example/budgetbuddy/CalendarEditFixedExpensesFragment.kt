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
import com.example.budgetbuddy.databinding.FragmentCalendarEditFixedExpensesBinding
import com.example.budgetbuddy.databinding.FragmentCalendarFixedExpensesBinding
import java.text.SimpleDateFormat
import java.util.Locale

private  const val ARG_ID = "documentId"
private const val ARG_NAME = "name"
private const val ARG_AMOUNT = "amount"
private const val ARG_REPEAT_FREQUENCY = "repeatFrequency"
private const val ARG_SELECTED_CATEGORY = "category"
private const val ARG_BEGIN_DATE ="beginDate"
private const val ARG_NEXT_DATE="nextDate"//для вычисления
private const val ARG_AMOUNT_OF_TRANS="amountOfTransfers"
private const val ARG_AMOUNT_OF_TRANS_TEMP="amountOfTransfersTemp"
private const val ARG_END_DAY_OF_TRANS="endDayOfTransfers"
private const val ARG_WHEN_STOP_FIXED_EXPENSE="whenStopFixedExpense"

class CalendarEditFixedExpensesFragment : Fragment() {


    private lateinit var binding: FragmentCalendarEditFixedExpensesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCalendarEditFixedExpensesBinding.inflate(inflater, container, false)
        val selectedDateText: TextView = binding.selectedDate31
        val calendarView : CalendarView =binding.calendarView31
        val confirmButton : Button = binding.confirmButtonFix31
        var nameV= ""
        var amountV=0.0
        val selectedId = arguments?.getString(ARG_ID)
        val categoryV = arguments?.getString(ARG_SELECTED_CATEGORY)
        val repeatV = arguments?.getString(ARG_REPEAT_FREQUENCY)
        val endPaymentDayV = arguments?.getString(ARG_END_DAY_OF_TRANS)
        val dayNext = arguments?.getString(ARG_NEXT_DATE)
        val whenStop=  arguments?.getString(ARG_WHEN_STOP_FIXED_EXPENSE)

        val amountTrans = arguments?.getInt(ARG_AMOUNT_OF_TRANS, 0)?:0
        val amountTransTemp = arguments?.getInt(ARG_AMOUNT_OF_TRANS_TEMP, 0)?:0

        arguments?.getString(ARG_BEGIN_DATE)?.let { selectedDate ->
            selectedDateText.text = selectedDate
        }

        setCurrentDate(selectedDateText)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth, selectedDateText)
        }

        arguments?.getString(ARG_NAME)?.let { selectedName ->
            nameV = selectedName
        }

        arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->
            amountV = selectedAmount
        }



        confirmButton.setOnClickListener {
            val selectedDate = selectedDateText.text.toString()
            val args = Bundle()
            args.putString("documentId",selectedId)
            args.putString("name", nameV)
            args.putDouble("amount",amountV)
            args.putString("repeatFrequency",repeatV)
            args.putString("category",categoryV)
            args.putString("beginDate",selectedDate)
            args.putString("nextDate", dayNext)
            args.putInt("amountOfTransfers", amountTrans )
            args.putString("endDayOfTransfers", endPaymentDayV)
            args.putInt("amountOfTransfersTemp", amountTransTemp)
            args.putString("whenStopFixedExpense", whenStop )

            val fef = EditFixedExpensesFragment.newInstance()
            fef.arguments = args
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fef)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        return binding.root


    }

    companion object {

        fun newInstance() =
            CalendarEditFixedExpensesFragment().apply {
                arguments = Bundle().apply {
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