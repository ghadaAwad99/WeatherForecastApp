package com.example.weatherforcast.network

import com.example.weatherforcast.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {
    @GET("onecall")
    suspend fun getCurrentTemp(@Query("lat") lat: Double,
                               @Query("lon") lon: Double,
                               @Query("appid") key: String,
                               @Query("lang") language: String,
                               @Query("units") unit: String,
                               ) : Response<WeatherModel>

}


//https://api.openweathermap.org/data/2.5/onecall?lat=31.200092&lon=29.918739&appid=8bdc89e28e3ae5c674e20f1d16e70f7d