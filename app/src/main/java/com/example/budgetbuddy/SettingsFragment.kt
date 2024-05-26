package com.example.budgetbuddy

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.budgetbuddy.databinding.FragmentSettingsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var db = Firebase.firestore

    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        firebaseRepository = FirebaseRepository(requireContext())

        val documentId = arguments?.getString("documentId")
        firebaseRepository.getName(
            onSuccess = { name->
                binding.nameChange.setText(name)
            },
            onFailure = {
                binding.nameChange.setText("")
            }
        )

        firebaseRepository.getSurname(
            onSuccess = { surname->
                binding.changeLastname.setText(surname)
            },
            onFailure = {
                binding.changeLastname.setText("")
            }
        )


        binding.saveButton.setOnClickListener {
            val name = binding.nameChange.text.toString()
            val surname = binding.changeLastname.text.toString()

            if(documentId!=null){
                updateUser(documentId, name, surname)
                 val home  = HomeFragment.newInstance()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, home)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        return binding.root
    }

    private fun updateUser(documentId: String, name: String, surname: String) {
      db.collection(userId).document("user")
          .update(
              mapOf(
                  "name" to name,
                  "surname" to surname
              )
          ).addOnSuccessListener {   Log.d(TAG, "Zmiany zostały zapisane") }
          .addOnFailureListener { e -> Log.w(TAG, "Zmiany nie zostały zapisane",e)  }
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