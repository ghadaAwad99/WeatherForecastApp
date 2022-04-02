package com.example.weatherforcast.home.view

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.model.Daily
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.DayItemBinding

import java.text.SimpleDateFormat
import java.util.*

class HomeDaysRecyclerAdapter : RecyclerView.Adapter<DaysViewHolder>() {
    lateinit var sharedPreferences : SharedPreferences

    private var daysList = mutableListOf<Daily>()

    fun setDaysList(daysList: List<Daily>) {
        this.daysList = daysList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = DayItemBinding.inflate(inflater, parent, false)
        return DaysViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        sharedPreferences = holder.binding.cardView.context.getSharedPreferences(holder.binding.cardView.context.getString(R.string.shared_prefs),
            AppCompatActivity.MODE_PRIVATE
        )
        var lang = sharedPreferences.getString("LANGUAGE", "en")
       var day = daysList[position]
       // var name = ""

        //Unix seconds
        val unix_seconds: Long = day.dt.toLong()
        //convert seconds to milliseconds
        val date = Date(unix_seconds * 1000L)
        // format of the date
        val jdf = SimpleDateFormat("EEE")
        jdf.timeZone = TimeZone.getTimeZone("GMT-4")
        var java_date = jdf.format(date).trimIndent()

            if(lang.equals("en")){
                when(position){
                0->java_date="Today"}

            }else if (lang.equals("ar")){
                when(position){
                    0->java_date="اليوم"
                1-> java_date = "غدا"}
                when(java_date){
                    "Sun" -> java_date = "الأحد"
                    "Mon" -> java_date = "الإثنين"
                    "Tue" -> java_date = "الثلاثاء"
                    "Wed" -> java_date = "الأربعاء"
                    "Thu" -> java_date = "الخميس"
                    "Fri" -> java_date = "الجمعة"
                    "Sat" -> java_date = "السبت"
                }
            }
        holder.binding.dayName.text = java_date

          /*  1-> java_date = "Tomorrow"*/
          /*  "Sat" -> name = "Saturday"
            "Sun" -> name = "Sunday"
            "Mon" -> name = "Monday"
            "Tue" -> name = "Tuesday"
            "Wed" -> name = "Wednesday"
            "Thu" -> name = "Thursday"
            "Fri" -> name = "Friday"*/




        holder.binding.dayTempMax.text = day.temp.max.toString() + " k"
        holder.binding.dayTempMin.text = day.temp.min.toString()+ " k"
        holder.binding.dayState.text = day.weather[0].description/*day.weather[0].main*/

        when(day.weather[0].main){
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
       /* holder.binding.dayName.text = day.dayName
        holder.binding.dayState.text = day.dayState
        holder.binding.dayTemp.text = day.dayTemp
        Glide.with(holder.itemView.context).load(day.dayIcon).into(holder.binding.dayWeatherIcon)*/
    }

    override fun getItemCount()= daysList.size
}
class DaysViewHolder(val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)