package com.example.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.databinding.ActivityLoginBinding
import com.example.budgetbuddy.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.regbutton.setOnClickListener{
            val email1 = binding.email1.text.toString()
            val password1 = binding.passw1.text.toString()
            val password2 = binding.passw2.text.toString()
            val imie = binding.imie.text.toString()
            val nazwisko = binding.nazwisko.text.toString()


            if( email1.isNotEmpty() && password1.isNotEmpty() && password2.isNotEmpty() && imie.isNotEmpty() && nazwisko.isNotEmpty() )
            {
                if( password1 == password2 ) {
                    firebaseAuth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            Toast.makeText(this, "Your account is created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, BudgeActivity::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }


        }
    }
    }
