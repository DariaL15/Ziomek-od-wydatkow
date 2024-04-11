package com.example.budgetbuddy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.budgetbuddy.databinding.FragmentCalendarBinding
import com.example.budgetbuddy.databinding.FragmentCategorySelectBinding
import com.example.budgetbuddy.databinding.FragmentCategorySelectBinding.*
import com.example.budgetbuddy.databinding.FragmentExpensesAddingBinding


class CategorySelectFragment : Fragment() {
    private lateinit var binding: FragmentCategorySelectBinding
    private val ARG_SELECTED_DATE = "selected_date"
    private  val ARG_AMOUNT = "selected_amount"
    private  val ARG_NOTES = "selected_notes"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentCategorySelectBinding.inflate(inflater, container, false)


        val radioGroup: RadioGroup = binding.categoryList
        val confirmButton: Button = binding.confirmButton4

        val view = binding.root
        var selectedRadioButtonId: String ="WYBIERZ"
        var dateV:String=""
        var notesV:String=""
        var amountV: Double = 0.00
        binding.house.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.car.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.clothes.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }
        binding.shopping.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.transport.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.sport.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.health.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.entertaiment.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.relax.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.restaurant.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }

        binding.gift.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val resourceName = buttonView.resources.getResourceEntryName(buttonView.id)
                selectedRadioButtonId = resourceName.substringAfter('/')
            }
        }




        confirmButton.setOnClickListener {

            arguments?.getString(ARG_SELECTED_DATE)?.let { selectedDate ->
                dateV= selectedDate
            }

            arguments?.getString(ARG_NOTES)?.let { selectedNotes ->
                notesV= selectedNotes
            }

            arguments?.getDouble(ARG_AMOUNT)?.let { selectedAmount ->
                amountV= selectedAmount
            }

            if (selectedRadioButtonId != "WYBIERZ") {

                val eaf = ExpensesAddingFragment.newInstance( dateV, selectedRadioButtonId,notesV,amountV)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, eaf)
                transaction.addToBackStack(null)
                transaction.commit()
            } else {
                Toast.makeText(requireContext(), "Proszę wybrać kategorię", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    companion object {

        fun newInstance(selectedDate: String, param2: String, selectedNotes:String, selectedAmount:Double) =
            CategorySelectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SELECTED_DATE, selectedDate)
                    putString(ARG_NOTES,selectedNotes)
                    putDouble(ARG_AMOUNT,selectedAmount)
                }
            }
    }
}