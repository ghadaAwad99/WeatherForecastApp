package com.example.weatherforcast

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherforcast.model.WeatherModel
import com.example.weatherforcast.settings.SettingsActivity
import java.io.IOException
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utilities : AppCompatActivity() {

    val ApiKey = "8bdc89e28e3ae5c674e20f1d16e70f7d"

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

    fun chooseWeatherIcon(status:String, currentIcon:ImageView){
        when (status) {
            "Clouds" -> currentIcon.setImageResource(R.drawable.current_cloudy)
            "Clear" -> currentIcon.setImageResource(R.drawable.current_sun)
            "Thunderstorm" -> currentIcon.setImageResource(R.drawable.cloudy_storm)
            "Drizzle" -> currentIcon.setImageResource(R.drawable.current_rain)
            "Rain" -> currentIcon.setImageResource(R.drawable.current_rain)
            "Snow" -> currentIcon.setImageResource(R.drawable.current_snow)
            "Mist" -> currentIcon.setImageResource(R.drawable.current_fog)
            "Smoke" -> currentIcon.setImageResource(R.drawable.current_fog)
            "Haze" -> currentIcon.setImageResource(R.drawable.current_fog)
            "Dust" -> currentIcon.setImageResource(R.drawable.current_fog)
            "Fog" -> currentIcon.setImageResource(R.drawable.current_fog)
            "Sand" -> currentIcon.setImageResource(R.drawable.current_fog)
            "Ash" -> currentIcon.setImageResource(R.drawable.current_fog)
            "Squall" -> currentIcon.setImageResource(R.drawable.current_squall)
            "Tornado" -> currentIcon.setImageResource(R.drawable.ic_tornado)
        }
    }

    fun convertToArabic(value: String): String {
        return (value + "")
            .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
            .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
            .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
            .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
            .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
    }

    fun convertDateAndTimeToMills(timeAndDate: String?): Long {
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val timeInMilliseconds: Long
        var mDate: Date? = null
        try {
            mDate = sdf.parse(timeAndDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.i("TAG", "Date m datee: $mDate")
        timeInMilliseconds = mDate!!.time
        println("Date in milli :: $timeInMilliseconds")
        return timeInMilliseconds
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }

}