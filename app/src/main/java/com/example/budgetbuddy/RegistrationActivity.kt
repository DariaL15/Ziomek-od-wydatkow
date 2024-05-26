package com.example.budgetbuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.storage.storage

class RegistrationActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val storage = Firebase.storage
    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView
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


        binding.addImgButton.setOnClickListener {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                } else {
                    pickImageFromGallery()
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
                } else {
                    pickImageFromGallery()
                }
            }
        }

        binding.regbutton.setOnClickListener {
            val email1 = binding.email1.text.toString()
            val password1 = binding.passw1.text.toString()
            val password2 = binding.passw2.text.toString()
            val imie = binding.imie.text.toString()
            val nazwisko = binding.nazwisko.text.toString()

            if (email1.isNotEmpty() && password1.isNotEmpty() && password2.isNotEmpty() && imie.isNotEmpty() && nazwisko.isNotEmpty()) {
                if (password1 == password2) {
                    firebaseAuth.createUserWithEmailAndPassword(email1, password1).addOnCompleteListener { registrationTask ->
                        if (registrationTask.isSuccessful) {
                            val currentUser = firebaseAuth.currentUser
                            val userId = currentUser?.uid ?: ""

                            if (imageUri != null) {
                                val storageRef = storage.reference.child("images/$userId/profile.jpg")
                                storageRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                                        val imageUrl = uri.toString()

                                        val userMap = hashMapOf(
                                            "name" to imie,
                                            "surename" to nazwisko,
                                            "email" to email1,
                                            "imageUrl" to imageUrl
                                        )

                                        db.collection(userId).document("user").set(userMap)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Konto zostało utworzone", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, BudgeActivity::class.java)
                                                startActivity(intent)
                                            }.addOnFailureListener { exception ->
                                                Log.e("TAG", "Błąd podczas przesyłania zdjęcia: ${exception.message}")
                                                Toast.makeText(this, "Nie udało się zarejestrować użytkownika. Spróbuj ponownie.", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }.addOnFailureListener { exception ->
                                    Toast.makeText(this, "Nie udało się dodać zdjęcia", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val userMap = hashMapOf(
                                    "name" to imie,
                                    "surename" to nazwisko,
                                    "email" to email1
                                )

                                db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Konto zostało utworzone", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, BudgeActivity::class.java)
                                        startActivity(intent)
                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(this, "Nie udało się zarejestrować użytkownika. Spróbuj ponownie.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Nie udało się zarejestrować użytkownika. Spróbuj ponownie.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Podane hasła różnią się", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Wypełnij wszytskie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage && resultCode == Activity.RESULT_OK) {

            imageUri = data?.data

            Glide.with(this)
                .load(imageUri)
                .transform(CircleCrop())
                .into(binding.addImgButton)
            binding.addImgText.text="ZDJĘCIE DODANE"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                pickImageFromGallery()
            } else {

                Toast.makeText(this, "Uprawnienie nie zostało przyznane", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
