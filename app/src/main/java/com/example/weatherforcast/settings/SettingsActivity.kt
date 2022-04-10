package com.example.weatherforcast.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.view.AlertsActivity
import com.example.weatherforcast.favorite.view.FavoriteActivity
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.home.view.MapsActivity
import com.google.android.material.navigation.NavigationView
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var languageRadioGroup: RadioGroup
    private lateinit var locationRadioGroup: RadioGroup
    private lateinit var tempRadioGroup: RadioGroup
    private lateinit var windRadioGroup: RadioGroup
    private lateinit var languageRadioButton: RadioButton
    private lateinit var locationRadioButton: RadioButton
    private lateinit var tempRadioButton: RadioButton
    private lateinit var windRadioButton: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TAG", "inside settings activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        this.title = getString(R.string.settings)
        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)

        drawerLayout= findViewById(R.id.drawerLayout)
        navView= findViewById(R.id.nav_view)
        languageRadioGroup = findViewById(R.id.language_radio_group)
        locationRadioGroup = findViewById(R.id.location_radio_group)
        tempRadioGroup = findViewById(R.id.temp_radio_group)
        windRadioGroup = findViewById(R.id.wind_radio_group)


        val sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        var lang = "en"


        languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            languageRadioButton = findViewById<View>(checkedId) as RadioButton
            Toast.makeText(this, languageRadioButton.text, Toast.LENGTH_SHORT).show()
            when(languageRadioButton.text){
                getString(R.string.arabic) -> lang = "ar"
                getString(R.string.english)-> lang = "en"
            }
            editor.putString("LANGUAGE", lang)
            editor.apply()

            Utilities.changeLanguage(lang, this)
            finish()
        }



        locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            locationRadioButton = findViewById<View>(checkedId) as RadioButton
            Toast.makeText(this, locationRadioButton.text, Toast.LENGTH_SHORT).show()

            when(locationRadioButton.text.toString()){

                getString(R.string.map) -> {
                    editor.putString("LOCATION", "Map")
                    editor.apply()
                    intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("source", "HOME")
                    startActivity(intent)
                    finish()}
                getString(R.string.gps)->{
                    editor.putString("LOCATION", "GPS")
                    editor.apply()
                    startActivity(Intent(this, HomeScreen::class.java))
                    finish()}
            }
        }

        tempRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            tempRadioButton = findViewById<View>(checkedId) as RadioButton
            Toast.makeText(this, tempRadioButton.text, Toast.LENGTH_SHORT).show()
            when(tempRadioButton.text.toString()){
                getString(R.string.celsius) -> {
                    editor.putString("TEMP", "celsius")
                    editor.apply()
                }
                getString(R.string.fehrenheit) -> {
                    editor.putString("TEMP", "fehrenheit")
                    editor.apply()
                }
                getString(R.string.kelvin)->{
                    editor.putString("TEMP", "kelvin")
                    editor.apply()
                }
            }
        }

        windRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            windRadioButton = findViewById<View>(checkedId) as RadioButton
            Toast.makeText(this, windRadioButton.text, Toast.LENGTH_SHORT).show()
            when(windRadioButton.text.toString()) {
                getString(R.string.meter_sec)->{
                    editor.putString("WIND", "meter_sec")
                    editor.apply()
                }
                getString(R.string.miles_hour)->{
                    editor.putString("WIND", "miles_hour")
                    editor.apply()
                }
            }
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home ->startActivity(Intent(this@SettingsActivity, HomeScreen::class.java))
                R.id.nav_settings -> startActivity(Intent(this@SettingsActivity, SettingsActivity::class.java))
                R.id.nav_favorite -> startActivity(Intent(this@SettingsActivity, FavoriteActivity::class.java))
                R.id.nav_alerts -> startActivity(Intent(this@SettingsActivity, AlertsActivity::class.java))

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
