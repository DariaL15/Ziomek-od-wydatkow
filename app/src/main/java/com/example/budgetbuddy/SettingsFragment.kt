package com.example.budgetbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val toolbarMenu = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_back)


        val textViewName = toolbarMenu?.findViewById<TextView>(R.id.nameofpageHP)
        textViewName?.text = "Edytuj profil"
        toolbarMenu?.visibility = View.VISIBLE
        toolbarBack?.visibility = View.GONE



    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}