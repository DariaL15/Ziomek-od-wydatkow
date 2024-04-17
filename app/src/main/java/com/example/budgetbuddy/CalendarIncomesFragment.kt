package com.example.budgetbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import com.example.budgetbuddy.databinding.FragmentCalendarBinding
import com.example.budgetbuddy.databinding.FragmentCalendarIncomesBinding
import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarIncomesFragment : Fragment() {

    private val ARG_SELECTED_CATEGORY = "selected_category"
    private  val ARG_AMOUNT = "selected_amount"
    private  val ARG_NOTES = "selected_notes"

    private lateinit var binding: FragmentCalendarIncomesBinding

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarIncomesBinding.inflate(inflater, container, false)
        val selectedDateText: TextView = binding.selectedDate1
        val calendarView: CalendarView = binding.calendarView1
        val confirmButton: Button = binding.confirmButtonIncomes2
        var categoryV:String=""
        var notesV:String=""
        var amountV: Double = 0.00


        setCurrentDate(selectedDateText)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth, selectedDateText)
        }

        confirmButton.setOnClickListener {

            arguments?.getString(ARG_SELECTED_CATEGORY)?.let { selectedDate ->
                categoryV= selectedDate
            }
            arguments?.getString(ARG_NOTES)?.let { selectedNotes ->
                notesV= selectedNotes
            }

            arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->
                amountV= selectedAmount
            }

            val selectedDate = selectedDateText.text.toString()
            val iaf = IncomesAddingFragment.newInstance(selectedDate,categoryV,notesV,amountV)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, iaf)
            transaction.addToBackStack(null)
            transaction.commit()
        }





        val view=binding.root
        return view

    }

    companion object {

        fun newInstance(param1: String, selectedCategory: String, selectedNotes:String, selectedAmount:Double) =
            CalendarIncomesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SELECTED_CATEGORY, selectedCategory)
                    putString(ARG_NOTES,selectedNotes)
                    putDouble(ARG_AMOUNT,selectedAmount)
                }
            }
    }

    private fun setCurrentDate(textView: TextView) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        textView.text = formattedDate
    }

    private fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int, textView: TextView) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        textView.text = formattedDate
    }
}