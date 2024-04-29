package com.example.budgetbuddy

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.budgetbuddy.databinding.FragmentEditFixedExpensesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditFixedExpensesFragment : Fragment() {

    private lateinit var binding: FragmentEditFixedExpensesBinding
    private lateinit var modelFixed: ModelFixed
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditFixedExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarMenu = activity?.findViewById<Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<Toolbar>(R.id.toolbar_back)
        toolbarMenu?.visibility = View.GONE
        toolbarBack?.visibility = View.VISIBLE

        val textViewName = toolbarBack?.findViewById<TextView>(R.id.nameofpageback)
        textViewName?.text = "Edytuj stałe zlecenie"

        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)

        imageViewBack?.setOnClickListener {
            val fragmentBack = ListOfFixedExpensesFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragmentBack)
                ?.addToBackStack(null)
                ?.commit()
        }

        val args = arguments
        if (args != null) {
            val name = args.getString("name", "")
            val amount = args.getDouble("amount", 0.0)
            val repeatFrequency = args.getString("repeatFrequency", "")
            val category = args.getString("category", "")
            val beginDate = args.getString("beginDate", "")
            val nextDate = args.getString("nextDate", "")
            val amountOfTransfers = args.getInt("amountOfTransfers", 0)
            val amountOfTransfersTemp = args.getInt("amountOfTransfersTemp", 0)
            val endDayOfTransfers = args.getString("endDayOfTransfers", "")




            binding.fixedExpenseName1.setText(name)
            binding.dateOfPayment1.setText(beginDate)

            binding.confirmButtonFix11.setOnClickListener {
                val updateName = binding.fixedExpenseName1.text.toString()
                val updateAmount = binding.amountOfPayments1
                val updateRepeatFraquency = binding.repeatSpinner1
                val updateCategory = binding.selectCategoryFixedExpensesSpinner1.selectedItem.toString()
                val updateBeginDate = binding.dateOfPayment1
                val updateNextDate = binding
                val updateAmountOfTransfers= binding
                val updateOfTransfersTemp = binding
                val updateEndDayOfTransfers = binding.dateOfEndOfPayment1

            }

        }
    }


    override fun onResume(){
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

    override fun onStop(){
        super.onStop()
        (activity as AppCompatActivity?)?.supportActionBar?.show()
    }
}