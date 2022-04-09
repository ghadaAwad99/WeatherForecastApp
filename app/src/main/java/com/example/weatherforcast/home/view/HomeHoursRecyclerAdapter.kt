package com.example.weatherforcast.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.model.Hourly
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.HourItemBinding
import java.text.SimpleDateFormat
import java.util.*


class HomeHoursRecyclerAdapter : RecyclerView.Adapter<HoursViewHolder>(){
    companion object{
        private var instance = HomeHoursRecyclerAdapter()
        fun getInstance():HomeHoursRecyclerAdapter{
            if (instance == null){
                instance = HomeHoursRecyclerAdapter()
            }
            return instance
        }
    }

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

        //holder.binding.hourName.text = hoursList[0].temp.toString()
        var hour = hoursList[position]
        
        when(position){
            0-> holder.binding.hourName.text = holder.binding.hourName.context.getString(R.string.now)
        }

        //Unix seconds
        val unix_seconds: Long = hour.dt.toLong()
        //convert seconds to milliseconds
        val date = Date(unix_seconds * 1000L)
        // format of the date
        //val jdf = SimpleDateFormat("EEE yyyy-MM-dd HH:mm")
        val jdf = SimpleDateFormat("HH:mm a")
        jdf.timeZone = TimeZone.getTimeZone("GMT+2")
        val java_date = jdf.format(date).trimIndent()
        holder.binding.hourName.text =java_date

        holder.binding.hourTemp.text = hour.temp.toString()
        holder.binding.hourWindSpeed.text = hour.wind_speed.toString() + " m/s"

        when(hour.weather[0].main){
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
