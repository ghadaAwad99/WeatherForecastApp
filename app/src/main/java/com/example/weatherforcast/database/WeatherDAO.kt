package com.example.weatherforcast.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.WeatherModel

@Dao
interface WeatherDAO {
    @Insert(onConflict = REPLACE)
    fun insertLastResponse(weatherModel: WeatherModel)

    @Query("SELECT * FROM weatherResponse")
    fun getLastResponse() : LiveData<WeatherModel>
}