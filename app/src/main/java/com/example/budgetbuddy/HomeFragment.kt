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
    ): View {



        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val paymentReminderDialog = PaymentReminderDialogFragment.newInstance()
        fragmentManager?.let { paymentReminderDialog.show(it, "PaymentReminderDialog") }

        binding.expenseButton.setOnClickListener {
            val expensesAddingFragment = ExpensesAddingFragment.newInstance(null,0,null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, expensesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.incomeButton.setOnClickListener {
            val incomesAddingFragment = IncomesAddingFragment.newInstance(null,null,null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, incomesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.catDom.setOnClickListener {
            val homeHistoryFragment = HomeHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, homeHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catAuto.setOnClickListener {
            val autoHistoryFragment = CarHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, autoHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catPrezent.setOnClickListener {
            val prezentHistoryFragment = PrezentHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, prezentHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catEdukacja.setOnClickListener {
            val edukacjaHistoryFragment = EdukacjaHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, edukacjaHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catRestauracje.setOnClickListener {
            val restauracjaHistoryFragment = RestauracjaHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, restauracjaHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catRozrywka.setOnClickListener {
            val rozrywkaHistoryFragment = RozrywkaHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, rozrywkaHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catZakupy.setOnClickListener {
            val zakupyHistoryFragment = ZakupyHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, zakupyHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catWypoczynek.setOnClickListener {
            val wypoczynekHistoryFragment = WypoczynekHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, wypoczynekHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }



        val view = binding.root
        firebaseRepository = FirebaseRepository(requireContext())
        val budgetmonthV = view.findViewById<TextView>(R.id.budgetmonth)
        firebaseRepository.getBudget(
            onSuccess = {budget ->
                val formattedBudget = String.format("%.2f", budget).replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1 ")
                budgetmonthV.text = formattedBudget

            },
            onFailure = {
                budgetmonthV.text = "0.00"
            }
        )

        val expensesV = view.findViewById<TextView>(R.id.expenses)
        firebaseRepository.getExpensesVal(
            onSuccess = {expenses ->
                val formattedSavings = String.format("%.2f", expenses).replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1 ")
                expensesV.text = formattedSavings

            },
            onFailure = {
                expensesV.text = "0.00"
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