package com.example.weatherforcast.model

import androidx.lifecycle.LiveData
import retrofit2.Response

interface RepositoryInterface {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        key: String,
        language: String,
        unit: String ) : Response<WeatherModel>

   suspend fun getLastResponseFromDB() : WeatherModel

    val allStoredFavorites: LiveData<List<FavoriteModel>>


    suspend fun getAllStoredAlarms() : List<UserAlarm>

    fun insertToFavorite(favoriteModel: FavoriteModel)

    fun deleteFromFavorite(favoriteModel: FavoriteModel)

    fun insertLastResponse(weatherModel: WeatherModel)

    fun insertAlarm(userAlarm: UserAlarm)
}