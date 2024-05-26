package com.example.budgetbuddy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.budgetbuddy.databinding.NavHeaderBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException


class Home : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var firebaseRepository: FirebaseRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val textViewImie = headerView.findViewById<TextView>(R.id.view_imie)
        val textViewNazwisko = headerView.findViewById<TextView>(R.id.view_nazwisko)
        val textViewEmail = headerView.findViewById<TextView>(R.id.view_email)
        val image = headerView.findViewById<ImageView>(R.id.imageView)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        firebaseRepository = FirebaseRepository(this)
        image.setImageResource(R.drawable.osoba)

        val storageRef = FirebaseStorage.getInstance().reference.child("images/$userId/profile.jpg")
        storageRef.metadata.addOnSuccessListener { _ ->
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                Glide.with(this)
                    .load(imageUrl)
                    .transform(CircleCrop())
                    .into(image)
            }.addOnFailureListener { exception ->
                Log.e("Home", "Nie udało sie pobrac zdjecia")
            }
        }.addOnFailureListener { exception ->
            if ((exception as StorageException).errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                Log.e("Home", "Zdjecie nie istnieje")

            } else {
                Log.e("Home", "Blad sprawdzenia czy zdjecie istnieje")
            }
        }






        firebaseRepository.getName(
            onSuccess = {name ->
                textViewImie.text = name


            },
            onFailure = {
               textViewImie.text = "Imię"

            }
        )

        firebaseRepository.getSurname(
            onSuccess = {surname ->
               textViewNazwisko.text = surname

            },
            onFailure = {
                textViewNazwisko.text = "Nazwisko"
            }
        )

        firebaseRepository.getEmail(
            onSuccess = {email ->
               textViewEmail.text = email

            },
            onFailure = {
                textViewEmail.text = "E-mail"
            }
        )



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_menu, R.string.closw_menu)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState ==null)
        {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem((R.id.nav_home))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.nav_home->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_fixed_epenses->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListOfFixedExpensesFragment()).commit()
            R.id.nav_settings->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
            R.id.nav_analytics->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AnalyticsFragment()).commit()
            R.id.nav_logout-> {
                logout()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Użytkownuk został wylogowany", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    override fun onBackPressed(){
        super.onBackPressed()
        if(drawerLayout. isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else
        {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}