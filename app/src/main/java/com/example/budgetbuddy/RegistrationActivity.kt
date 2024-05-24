package com.example.budgetbuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.databinding.ActivityLoginBinding
import com.example.budgetbuddy.databinding.ActivityRegistrationBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class RegistrationActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val IMAGE_PICK_REQUEST_CODE = 1001
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



            Log.d("RegistrationActivity", "Przed wywołaniem launch()")



            if (email1.isNotEmpty() && password1.isNotEmpty() && password2.isNotEmpty() && imie.isNotEmpty() && nazwisko.isNotEmpty()) {
                if (password1 == password2) {
                    firebaseAuth.createUserWithEmailAndPassword(email1, password1).addOnCompleteListener { registrationTask ->
                        if (registrationTask.isSuccessful) {
                            val currentUser = firebaseAuth.currentUser
                            val userId = currentUser?.uid ?: ""

                            val userMap = hashMapOf(
                                "name" to imie,
                                "surename" to nazwisko,
                                "email" to email1
                            )

                            db.collection(userId).document("user").set(userMap).addOnSuccessListener {
                                Toast.makeText(this, "Your account is created", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, BudgeActivity::class.java)
                                startActivity(intent)
                            }.addOnFailureListener { exception ->
                                Toast.makeText(this, "Failed to add user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Failed to register user: ${registrationTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }


        }
    }
    }
