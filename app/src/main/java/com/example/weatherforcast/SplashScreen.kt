package com.example.weatherforcast

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import com.example.weatherforcast.home.view.HomeScreen
import java.util.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // hide the status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            val sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE)

            val lang = sharedPreferences.getString("LANGUAGE", "en")

            val config = this.resources.configuration

            val locale = Locale(lang)
            Locale.setDefault(locale)
            config.setLocale(locale)

            this.createConfigurationContext(config)
            this.resources.updateConfiguration(config, this.resources.displayMetrics)
            if (!Utilities.isOnline(this)) {this.startActivity(Intent(this, HomeScreen::class.java)) }
            else{ this.startActivity(Intent(this, MainActivity::class.java)) }
            finish()
        }, 4000)
    }

}