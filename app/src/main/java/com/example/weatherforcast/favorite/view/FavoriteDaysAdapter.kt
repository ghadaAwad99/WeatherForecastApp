package com.example.weatherforcast.favorite.view

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.databinding.DayItemBinding
import com.example.weatherforcast.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class FavoriteDaysAdapter : RecyclerView.Adapter<FavoriteDaysAdapter.FavDaysViewHolder>() {
    lateinit var sharedPreferences: SharedPreferences

    private var daysList = mutableListOf<Daily>()

    fun setFavDaysList(daysList: List<Daily>) {
        this.daysList = daysList.toMutableList()
        notifyDataSetChanged()
    }

    class FavDaysViewHolder(val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavDaysViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DayItemBinding.inflate(inflater, parent, false)
        return FavDaysViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavDaysViewHolder, position: Int) {
        sharedPreferences = holder.binding.cardView.context.getSharedPreferences(
            holder.binding.cardView.context.getString(
                R.string.shared_prefs
            ),
            AppCompatActivity.MODE_PRIVATE
        )
        var lang = sharedPreferences.getString("LANGUAGE", "en")
        var day = daysList[position]

        val unix_seconds: Long = day.dt.toLong()
        val date = Date(unix_seconds * 1000L)
        val jdf = SimpleDateFormat("EEE")
        jdf.timeZone = TimeZone.getTimeZone("GMT+2")
        var java_date = jdf.format(date).trimIndent()

        /*  if(lang.equals("en")){
               when(position){
                   0->java_date="Today"}

           }else*/ if (lang.equals("ar")) {
            /*   when(position){
                   0->java_date="اليوم"
                   1-> java_date = "غدا"}*/
            when (java_date) {
                "Sun" -> java_date = "الأحد"
                "Mon" -> java_date = "الإثنين"
                "Tue" -> java_date = "الثلاثاء"
                "Wed" -> java_date = "الأربعاء"
                "Thu" -> java_date = "الخميس"
                "Fri" -> java_date = "الجمعة"
                "Sat" -> java_date = "السبت"
            }
        }
        // }
        holder.binding.dayName.text = java_date
        holder.binding.dayTempMax.text = day.temp.max.toString()
        holder.binding.dayTempMin.text = day.temp.min.toString()
        holder.binding.dayState.text = day.weather[0].description/*day.weather[0].main*/


        //chooseWeatherIcon(day.weather[0].main, holder.binding.dayWeatherIcon)
       when (day.weather[0].main) {
            "Clouds" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.cloudy)
            "Clear" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.sun)
            "Thunderstorm" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.thunderstorm)
            "Drizzle" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.drizzle)
            "Rain" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.rain)
            "Snow" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.snow)
            "Mist" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.mist)
            "Smoke" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.smoke)
            "Haze" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.haze)
            "Dust" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.dust)
            "Fog" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.fog)
            "Sand" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.dust)
            "Ash" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.haze)
            "Squall" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.squall)
            "Tornado" -> holder.binding.dayWeatherIcon.setImageResource(R.drawable.ic_tornado)
        }
    }

    override fun getItemCount() = daysList.size
}