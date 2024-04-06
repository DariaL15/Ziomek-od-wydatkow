package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Locale




class CalendarFragment : Fragment() {

    private val ARG_SELECTED_CATEGORY = "selected_category"

    private lateinit var binding: FragmentCalendarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val selectedDateText: TextView = binding.selectedDate
        val calendarView: CalendarView = binding.calendarView
        val confirmButton: Button = binding.confirmButton3
        var categoryV:String=""

        setCurrentDate(selectedDateText)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth, selectedDateText)
        }

        confirmButton.setOnClickListener {

            arguments?.getString(ARG_SELECTED_CATEGORY)?.let { selectedDate ->
                categoryV= selectedDate
            }

            val selectedDate = selectedDateText.text.toString()
            val eaf = ExpensesAddingFragment.newInstance(selectedDate,categoryV)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, eaf)
            transaction.addToBackStack(null)
            transaction.commit()
            }





        val view=binding.root
        return view
    }

    companion object {



        fun newInstance(param1: String, selectedCategory: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SELECTED_CATEGORY, selectedCategory)

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
}}