package com.example.weatherforcast

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherforcast.settings.SettingsActivity
import java.io.IOException
import java.lang.StringBuilder
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

    fun getAddress(latitude: Double, longitude: Double, lang:String, context: Context): String {
        val result = StringBuilder()
        try {
            val geocoder = Geocoder(context, Locale(lang))
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                if(address.locality != null && address.locality.isNotEmpty()){
                    result.append(address.locality)
                }else if (address.getAddressLine(0) != null && address.getAddressLine(0).isNotEmpty()){
                    result.append(address.getAddressLine(0))
                }else{
                    result.append("Unknown")
                }

            }
        } catch (e: IOException) {
            Log.e("TAG", e.message.toString())
        }
        return result.toString()
    }
}