package com.example.weatherforcast.model

import com.example.weatherforcast.network.WeatherInterface
import com.example.weatherforcast.network.WeatherService

class Repository private constructor(
    private val weatherInterface: WeatherInterface
    /* var remoteSource : RemoteSource,
     var localSource : LocalSource,
     var context : Context*/
) : RepositoryInterface {


    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        key: String,
        language: String,
        unit: Double
    ) = weatherInterface.getCurrentTemp(lat, lon, key, language, unit)


    companion object {
        private var instance: Repository? = null
        fun getInstance(): Repository {
            //Log.i("TAG", "inside repo get instance")
            return instance ?: Repository(
                /*remoteSource, localSource, context*/
                WeatherService.getInstance(),
            )
        }
    }
}