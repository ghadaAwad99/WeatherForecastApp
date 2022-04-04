package com.example.weatherforcast.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weatherResponse")

data class WeatherModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:Int,
    @ColumnInfo(name = "current")
    val current: Current,
    @ColumnInfo(name = "daily")
    val daily: List<Daily>,
    @ColumnInfo(name = "hourly")
    val hourly: List<Hourly>,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,
    //val timezone: String,
    //val timezone_offset: Double
)