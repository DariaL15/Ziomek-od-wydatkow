package com.example.budgetbuddy


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.budgetbuddy.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {


    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.expenseButton.setOnClickListener {
            val expensesAddingFragment = ExpensesAddingFragment.newInstance("?","?")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, expensesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }



        val view = binding.root
        firebaseRepository = FirebaseRepository(requireContext())
        val budgetmonthV = view.findViewById<TextView>(R.id.budgetmonth)
        firebaseRepository.getBudget(
            onSuccess = {budget ->
                budgetmonthV.text="$budget"

            },
            onFailure = {errorMessage ->
                budgetmonthV.text = "0.0"
            }
        )

        val savingsV = view.findViewById<TextView>(R.id.expenses)
        firebaseRepository.getSavings(
            onSuccess = {savings ->
                savingsV.text="$savings"

            },
            onFailure = {errorMessage ->
                savingsV.text = "0.0"
            }
        )
        return view
    }

    companion object {

        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}