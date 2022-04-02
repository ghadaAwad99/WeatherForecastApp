package com.example.weatherforcast.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import com.example.weatherforcast.model.WeatherModel

@Dao
interface WeatherDAO {
    @Insert(onConflict = REPLACE)
    fun insertLastResponse(weatherModel: WeatherModel)
}