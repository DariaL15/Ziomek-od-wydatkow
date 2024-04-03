package com.example.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.databinding.ActivityBudgeBinding
import com.example.budgetbuddy.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class BudgeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgeBinding
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var dbb = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBudgeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.budgetbutton.setOnClickListener{
            val savings = binding.savings.text.toString().toDoubleOrNull()
            val budget = binding.budget.text.toString().toDoubleOrNull()

            val budgetMap = hashMapOf(
                "budgetV" to budget,
                "savingsV" to savings

            )

            if( savings != null && budget != null )
            {
                dbb.collection(userId).document("budget").set(budgetMap).addOnSuccessListener {
                    Toast.makeText(this,"Budget added",Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(this,"Not added",Toast.LENGTH_SHORT).show()
                    }
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this, "Wprowadź swój budget", Toast.LENGTH_SHORT).show()
            }
        }

    }
}