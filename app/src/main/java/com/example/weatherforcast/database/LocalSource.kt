package com.example.weatherforcast.database

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.UserAlarm
import com.example.weatherforcast.model.WeatherModel

interface LocalSource {
    val allStoredFavorites: LiveData<List<FavoriteModel>>
    fun insertToFavorite(favoriteModel: FavoriteModel)
    fun deleteFromFavorite(favoriteModel: FavoriteModel)
    fun deleteAlarm(alarm: UserAlarm)
    fun insertLastResponse(weatherModel: WeatherModel)
    suspend fun getLastResponseFromDB() : WeatherModel
    suspend fun getStoredAlarms() : List<UserAlarm>
    fun insertAlarm(userAlarm: UserAlarm)
}