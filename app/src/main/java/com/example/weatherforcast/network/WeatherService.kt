package com.example.weatherforcast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherService {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    fun getInstance() : WeatherInterface {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherInterface::class.java)
    }


}