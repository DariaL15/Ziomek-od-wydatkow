package com.example.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:com.example.budgetbuddy.databinding.ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signup.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.logbutton.setOnClickListener{
            val email = binding.email.text.toString()
            val password = binding.passw.text.toString()
            if (isNetworkAvailable(this)) {
            if( email.isNotEmpty() && password.isNotEmpty() )
            {
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful)
                    {
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this, "Nie udało się zalogować. Zły e-mail lub hasło. Spróbuj ponownie.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
            else {
                Toast.makeText(this, "Brak połączenia z internetem. Sprawdź swoje połączenie i spróbuj ponownie.", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            if (network == null) {
                return false
            }
            val activeNetwork = connectivityManager.getNetworkCapabilities(network)
            if (activeNetwork == null) {

                return false
            }

            val hasInternetCapability = activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val hasValidatedCapability = activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            val hasNotMeteredCapability = activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)


            return hasInternetCapability && hasValidatedCapability
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            val isConnected = networkInfo?.isConnected ?: false

            return isConnected
        }
    }
}