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
import com.example.budgetbuddy.databinding.FragmentCalendarBinding
import com.example.budgetbuddy.databinding.FragmentCalendarEditExpensesBinding
import java.text.SimpleDateFormat
import java.util.Locale
//отредактировать календарь. Сделать его красным

class CalendarEditExpensesFragment : Fragment() {

    private  val ARG_SELECTED_CATEGORY_POSITION = "categoryPos"
    private  val ARG_SELECTED_CATEGORY_V = "collection"
    private  val ARG_AMOUNT = "amount"
    private  val ARG_NOTES = "notes"
    private  val ARG_SELECTED_DATE = "date"
    private val ARG_ID = "documentId"
    private val ARG_ORG_COLLECTION_V ="oldCollection"


    private lateinit var binding: FragmentCalendarEditExpensesBinding
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
        binding = FragmentCalendarEditExpensesBinding.inflate(inflater, container, false)
        val selectedDateText: TextView = binding.selectedDate32
        val calendarView: CalendarView = binding.calendarView32
        val confirmButton: Button = binding.confirmButtonFix32
        val categoryPos = arguments?.getInt(ARG_SELECTED_CATEGORY_POSITION, 0) ?: 0
        val categoryV=arguments?.getString(ARG_SELECTED_CATEGORY_V)
        val selectedId = arguments?.getString(ARG_ID)
        val orgCollection=arguments?.getString(ARG_ORG_COLLECTION_V)
        var notesV = ""
        var amountV = 0.00

        arguments?.getString(ARG_SELECTED_DATE)?.let {  selectedDate ->
            selectedDateText.text = selectedDate
        }
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
            val args = Bundle()
            args.putString("documentId",selectedId)
            args.putString("notes", notesV)
            args.putDouble("amount",amountV)
            args.putString("date",selectedDate)
            args.putString("collection",categoryV)
            args.putInt("categoryPos",categoryPos)
            args.putString("oldCollection", orgCollection)

            val eaf = EditExpensesFragment.newInstance()
            eaf.arguments = args
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, eaf)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        return binding.root
    }

    companion object {
        fun newInstance() =
            CalendarEditExpensesFragment().apply {
                arguments = Bundle().apply {

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