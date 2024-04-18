package com.example.budgetbuddy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Home : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
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
                .replace(R.id.fragment_container, FixedExpenseFragment()).commit()
            R.id.nav_settings->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
            R.id.nav_savings->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SavingsFragment()).commit()
            R.id.nav_debt->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DebtFragment()).commit()
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