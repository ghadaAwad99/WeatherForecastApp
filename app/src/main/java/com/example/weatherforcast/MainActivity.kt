package com.example.weatherforcast

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.weatherforcast.home.view.HomeScreen
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var radioButton: RadioButton
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)
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






        val okButton: Button = findViewById(R.id.ok_button)
        val radioGroup: RadioGroup = findViewById(R.id.location_radio_group)
        okButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            radioButton = findViewById(selectedId)
            Toast.makeText(this@MainActivity, radioButton.text, Toast.LENGTH_SHORT).show()
            //save location choice on shared prefs
            val editor = sharedPreferences.edit()
            editor.putString("LOCATION", radioButton.text.toString())
            editor.apply()

            val intent = Intent(this@MainActivity, HomeScreen::class.java)
            startActivity(intent)
        }

        val actionBar: ActionBar = supportActionBar!!
        val colorDrawable = ColorDrawable(Color.parseColor("#5B86E5"))
        actionBar.setBackgroundDrawable(colorDrawable)
    }


}