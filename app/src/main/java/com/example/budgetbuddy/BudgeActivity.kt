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

class BudgeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBudgeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.budgetbutton.setOnClickListener{
            val savings = binding.savings.toString()
            val budget = binding.budget.toString()

            if( savings.isNotEmpty() && budget.isNotEmpty() )
            {
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