package com.example.weatherforcast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherforcast.settings.SettingsActivity
import java.util.*

object Utilities : AppCompatActivity() {
    fun changeLanguage(lang:String, context: Context){
        val config = context.resources.configuration

        val locale = Locale(lang)
        Locale.setDefault(locale)
        config.setLocale(locale)

        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.startActivity(Intent(context, context::class.java))

    }
}