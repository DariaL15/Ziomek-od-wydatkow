package com.example.budgetbuddy

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Locale




class CalendarFragment : Fragment() {

    private  val ARG_SELECTED_CATEGORY_POSITION = "selected_category_position"
    private  val ARG_AMOUNT = "selected_amount"
    private  val ARG_NOTES = "selected_notes"

    private lateinit var binding: FragmentCalendarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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

        toolbarBack?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.redlight))


        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)

        imageViewBack?.setOnClickListener(null)
        imageViewBack?.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val selectedDateText: TextView = binding.selectedDate
        val calendarView: CalendarView = binding.calendarView
        val confirmButton: Button = binding.confirmButton3
        val categoryV = arguments?.getInt(ARG_SELECTED_CATEGORY_POSITION, 0) ?: 0
        var notesV = ""
        var amountV = 0.00

        setCurrentDate(selectedDateText)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth, selectedDateText)
        }

        confirmButton.setOnClickListener {


            arguments?.getString(ARG_NOTES)?.let { selectedNotes ->
                notesV = selectedNotes
            }

            arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->
                amountV = selectedAmount
            }

            val selectedDate = selectedDateText.text.toString()
            val eaf = ExpensesAddingFragment.newInstance(selectedDate, categoryV, notesV, amountV)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, eaf)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        return binding.root
    }

    companion object {



        fun newInstance(
            selectedCategoryPosition: Int,
            selectedNotes: String,
            selectedAmount: Double
        ) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SELECTED_CATEGORY_POSITION, selectedCategoryPosition)
                    putString(ARG_NOTES,selectedNotes)
                    putDouble(ARG_AMOUNT,selectedAmount)
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
}}