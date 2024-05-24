package com.example.budgetbuddy

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.budgetbuddy.databinding.NavHeaderBinding
import com.google.android.material.navigation.NavigationView

class Home : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

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

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        firebaseRepository = FirebaseRepository(this)
        firebaseRepository.getName(
            onSuccess = {name ->
                textViewImie.text = name
                Log.d("Home", "Imię użytkownika: $name")

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
            R.id.nav_logout->Toast.makeText(this, "Wylogowane", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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