package com.example.weatherforcast.model

import retrofit2.Response

interface RepositoryInterface {
    suspend fun getCurrentWeather(lat : Double,
                                  lon : Double,
                                  key : String,
                                  language : String = "",
                                  unit : Double = 0.0,) : Response<WeatherModel>
}