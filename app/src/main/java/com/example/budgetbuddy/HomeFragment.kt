package com.example.budgetbuddy


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.example.budgetbuddy.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {


    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var binding: FragmentHomeBinding
    private lateinit var paymentReminderDialogFragment: PaymentReminderDialogFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


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

        binding.catTransport.setOnClickListener{
            val transportHistoryFragment =TransportHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, transportHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.catSport.setOnClickListener {
            val sportHistoryFragment = SportHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, sportHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.catUbrania.setOnClickListener{
            val ubranieHistoryFragment =UbranieHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, ubranieHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.catZdrowie.setOnClickListener{
            val healthHistoryFragment =HealthHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, healthHistoryFragment)
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

        val paymentReminderDialog = PaymentReminderDialogFragment()
            paymentReminderDialog.show(childFragmentManager, "PaymentReminderDialog")




        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        paymentReminderDialogFragment = PaymentReminderDialogFragment()

        val toolbarMenu = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_back)


        val textViewName = toolbarMenu?.findViewById<TextView>(R.id.nameofpageHP)
        textViewName?.text = "Strona główna"
        toolbarMenu?.visibility = View.VISIBLE
        toolbarBack?.visibility = View.GONE



    }


    companion object {

        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}