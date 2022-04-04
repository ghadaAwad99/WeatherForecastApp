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

    val storedResponse: LiveData<WeatherModel>

    val allStoredFavorites: LiveData<List<FavoriteModel>>

    fun insertToFavorite(favoriteModel: FavoriteModel)

    fun deleteFromFavorite(favoriteModel: FavoriteModel)

    fun insertLastResponse(weatherModel: WeatherModel)
}