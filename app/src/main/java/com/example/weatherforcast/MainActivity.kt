package com.example.weatherforcast

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.weatherforcast.home.view.HomeScreen
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.weatherforcast.home.view.MapsActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var locationRadioGroup: RadioGroup
    lateinit var locationRadioButton:RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationRadioGroup = findViewById(R.id.location_radio_group)
        val sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        //set language as the last selected
       /* var lang = sharedPreferences.getString("LANGUAGE", "en")
        val config = resources.configuration

        val locale = Locale(lang)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(locale)
        else
            config.locale = locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)*/

      /*  val lang = sharedPreferences.getString("LANGUAGE", "en")

        val config = this.resources.configuration

        val locale = Locale(lang)
        Locale.setDefault(locale)
        config.setLocale(locale)

        this.createConfigurationContext(config)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)

        this.startActivity(Intent(this, MainActivity::class.java))*/
        //finish()

        //Utilities.changeLanguage(lang.toString(), this)

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





        val radioGroup: RadioGroup = findViewById(R.id.location_radio_group)

        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)
    }


}