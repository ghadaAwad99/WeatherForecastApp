package com.example.weatherforcast.home.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.weatherforcast.R
import com.example.weatherforcast.database.AppDatabase
import com.example.weatherforcast.databinding.DayItemBinding
import com.example.weatherforcast.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class HomeDaysRecyclerAdapter : RecyclerView.Adapter<DaysViewHolder>() {

    companion object{
        private var instance = HomeDaysRecyclerAdapter()
        fun getInstance():HomeDaysRecyclerAdapter{
            if (instance == null){
                instance = HomeDaysRecyclerAdapter()
            }
            return instance
        }
    }
/*    companion object{
        private var instance : AppDatabase? =null
        @Synchronized
        fun getInstance(context: Context) : AppDatabase {
            return instance?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "WeatherDatabase"
            ).fallbackToDestructiveMigration().build()
        }
    }*/
    lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = holder.binding.cardView.context.getSharedPreferences(
            holder.binding.cardView.context.getString(R.string.shared_prefs),
            AppCompatActivity.MODE_PRIVATE
        )
        var lang = sharedPreferences.getString("LANGUAGE", "en")
        var day = daysList[position]

        val unix_seconds: Long = day.dt.toLong()
        val date = Date(unix_seconds * 1000L)
        val jdf = SimpleDateFormat("EEE")
        jdf.timeZone = TimeZone.getTimeZone("GMT+2")
        var java_date = jdf.format(date).trimIndent()

        if (lang.equals("ar")) {
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
        holder.binding.dayName.text = java_date
        holder.binding.dayTempMax.text = day.temp.max.toString() + " k"
        holder.binding.dayTempMin.text = day.temp.min.toString() + " k"
        holder.binding.dayState.text = day.weather[0].description/*day.weather[0].main*/

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
        /* holder.binding.dayName.text = day.dayName
         holder.binding.dayState.text = day.dayState
         holder.binding.dayTemp.text = day.dayTemp
         Glide.with(holder.itemView.context).load(day.dayIcon).into(holder.binding.dayWeatherIcon)*/
    }

    override fun getItemCount() = daysList.size
}

class DaysViewHolder(val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)