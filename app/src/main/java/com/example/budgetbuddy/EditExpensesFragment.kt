package com.example.budgetbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.budgetbuddy.databinding.FragmentEditExpensesBinding
import com.example.budgetbuddy.databinding.FragmentEditFixedExpensesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth


class EditExpensesFragment : Fragment() {

    private lateinit var binding: FragmentEditExpensesBinding
    private lateinit var modelFixed: ModelFixed
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentEditExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbarBack = activity?.findViewById<Toolbar>(R.id.toolbar_back)
        val toolbarMenu = requireActivity().findViewById<Toolbar>(R.id.toolbar)

        toolbarMenu?.visibility = View.GONE
        toolbarBack?.visibility = View.VISIBLE

        val textViewName = toolbarMenu?.findViewById<TextView>(R.id.nameofpageHP)
        textViewName?.text = "Edytuj swoje wypłaty"


        val imageViewBack = toolbarBack?.findViewById<ImageView>(R.id.imageView)

        imageViewBack?.setOnClickListener {
            val fragmentBack = HomeFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragmentBack)
                ?.addToBackStack(null)
                ?.commit()
        }

        val args = arguments
        if (args != null) {
            val notes = args.getString("notes", "")
            val amount = args.getDouble("amount", 0.0)
            val category = args.getString("category", "")
            val date = args.getString("date", "")

            binding.noteEditText1.setText(notes)
            binding.amount1.setText(amount.toString())
            binding.selectDateEnd1.setText(date)
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