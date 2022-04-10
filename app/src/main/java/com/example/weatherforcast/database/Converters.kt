package com.example.weatherforcast.database

import androidx.room.TypeConverter
import com.example.weatherforcast.model.Alerts
import com.example.weatherforcast.model.Current
import com.example.weatherforcast.model.Daily
import com.example.weatherforcast.model.Hourly
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromCurrentToString(current: Current) = Gson().toJson(current)
    @TypeConverter
    fun fromStringToCurrent(stringCurrent : String) = Gson().fromJson(stringCurrent, Current::class.java)

    @TypeConverter
    fun fromDailyListToString(daily: List<Daily>) = Gson().toJson(daily)
    @TypeConverter
    fun fromStringToDailyList(stringDaily : String) = Gson().fromJson(stringDaily, Array<Daily>::class.java).toList()

    @TypeConverter
    fun fromHourlyListToString(hourly: List<Hourly>) = Gson().toJson(hourly)
    @TypeConverter
    fun fromStringToHourlyList(stringHourly : String) = Gson().fromJson(stringHourly, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun fromAlertToString(alerts: List<Alerts?>?) = Gson().toJson(alerts)
    @TypeConverter
    fun fromStringToAlert(stringAlert : String?) = Gson().fromJson(stringAlert, Array<Alerts?>::class.java)?.toList()

}