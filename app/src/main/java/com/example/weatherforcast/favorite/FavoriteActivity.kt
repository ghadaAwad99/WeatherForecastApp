package com.example.weatherforcast.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherforcast.R
import com.example.weatherforcast.alerts.AlertsActivity
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView

class FavoriteActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        drawerLayout= findViewById(R.id.drawerLayout)
        navView= findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home ->startActivity(Intent(this@FavoriteActivity, HomeScreen::class.java))
                R.id.nav_settings -> startActivity(Intent(this@FavoriteActivity, SettingsActivity::class.java))
                R.id.nav_favorite -> startActivity(Intent(this@FavoriteActivity, FavoriteActivity::class.java))
                R.id.nav_alerts -> startActivity(Intent(this@FavoriteActivity, AlertsActivity::class.java))
            }
            true
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    }
