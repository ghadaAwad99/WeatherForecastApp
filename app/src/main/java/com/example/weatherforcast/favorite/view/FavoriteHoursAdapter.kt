package com.example.weatherforcast.favorite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.databinding.HourItemBinding
import com.example.weatherforcast.home.view.HoursViewHolder
import com.example.weatherforcast.model.Hourly
import java.text.SimpleDateFormat
import java.util.*

class FavoriteHoursAdapter : RecyclerView.Adapter<FavHoursViewHolder>(){

    private var hoursList = mutableListOf<Hourly>()

    fun setFavHoursList(hoursList: List<Hourly>) {
        this.hoursList = hoursList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavHoursViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HourItemBinding.inflate(inflater, parent, false)
        return FavHoursViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavHoursViewHolder, position: Int) {
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
        //chooseWeatherIcon(hour.weather[0].main, holder.binding.hourWeatherIcon)

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

    override fun getItemCount() = hoursList.size
}
class FavHoursViewHolder(val binding: HourItemBinding) : RecyclerView.ViewHolder(binding.root)


/*fun chooseWeatherIcon(status:String, currentIcon: ImageView){
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
}*/
