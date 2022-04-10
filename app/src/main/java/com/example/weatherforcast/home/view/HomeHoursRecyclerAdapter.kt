package com.example.weatherforcast.home.view

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.model.Hourly
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.databinding.HourItemBinding
import java.text.SimpleDateFormat
import java.util.*


class HomeHoursRecyclerAdapter : RecyclerView.Adapter<HoursViewHolder>(){

    private var hoursList = mutableListOf<Hourly>()

    fun setHoursList(hoursList: List<Hourly>) {
        this.hoursList = hoursList.toMutableList()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HourItemBinding.inflate(inflater, parent, false)
        return HoursViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        lateinit var sharedPreferences: SharedPreferences
        sharedPreferences = holder.binding.cardView.context.getSharedPreferences(
            holder.binding.cardView.context.getString(R.string.shared_prefs),
            AppCompatActivity.MODE_PRIVATE)

        var lang = sharedPreferences.getString("LANGUAGE", "en")
        var sharedPrefsTemp = sharedPreferences.getString("TEMP", "celsius").toString()

        var wind = sharedPreferences.getString("WIND","meter_sec")
        var currentHour = hoursList[position]

        val dateFormat = SimpleDateFormat("h:mm a",Locale(lang))
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+2")

        holder.binding.hourName.text = dateFormat.format(currentHour.dt.toLong()*1000)

        lateinit var temp: String
        lateinit var unit: String
        lateinit var windSpeed: String
        lateinit var windUnit: String

        if (lang.equals("ar")) {
            when(sharedPrefsTemp ){
                "celsius" ->{ temp = Utilities.convertToArabic(currentHour.temp.toString())
                     unit = " ºC"}
                "fehrenheit" -> {
                    temp = Utilities.convertToArabic((currentHour.temp/2+30).toString())
                     unit = " ف"
                }
                "kelvin" -> {
                    temp = Utilities.convertToArabic((currentHour.temp + 273.15).toString())
                    unit = " ك"
                }
            }
            when(wind){
                "meter_sec" -> {
                    windSpeed = Utilities.convertToArabic(currentHour.wind_speed.toString())
                    windUnit = "م/ث"
                }
                "miles_hour" -> {
                    windSpeed = Utilities.convertToArabic((currentHour.wind_speed * 2).toString())
                    windUnit = "م/س"
                }
            }
        }else if (lang == "en") {
            when(sharedPrefsTemp ){
                "celsius" ->{ temp = currentHour.temp.toString()
                    unit = " ºC"}
                "fehrenheit" -> {
                    temp = (currentHour.temp/2+30).toString()
                    unit = " ºF"
                }
                "kelvin" -> {
                    temp = (currentHour.temp + 273.15).toString()
                    unit = " k"
                }
            }
            when(wind){
                "meter_sec" -> {
                    windSpeed = currentHour.wind_speed.toString()
                    windUnit = "m/s"
                }
                "miles_hour" -> {
                    windSpeed = (currentHour.wind_speed * 2).toString()
                    windUnit = "m/h"
                }
            }
        }


        holder.binding.hourTemp.text = temp + unit
        holder.binding.hourWindSpeed.text = "$windSpeed $windUnit"

        when(currentHour.weather[0].main){
            "Clouds" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.cloudy)
            "Clear" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.sun)
            "Thunderstorm" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.thunderstorm)
            "Drizzle" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.drizzle)
            "Rain" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.rain)
            "Snow" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.snow)
            "Mist" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.mist)
            "Smoke" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.smoke)
            "Haze" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.haze)
            "Dust" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.dust)
            "Fog" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.fog)
            "Sand" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.dust)
            "Ash" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.haze)
            "Squall" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.squall)
            "Tornado" -> holder.binding.hourWeatherIcon.setImageResource(R.drawable.ic_tornado)
        }


    }

    override fun getItemCount()= hoursList.size
}


class HoursViewHolder(val binding: HourItemBinding) : RecyclerView.ViewHolder(binding.root)
