package com.example.weatherforcast.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherforcast.database.LocalSource
import com.example.weatherforcast.network.WeatherInterface
import com.example.weatherforcast.network.WeatherService

class Repository private constructor(
    private val weatherInterface: WeatherInterface,
     var localSource : LocalSource,
    var context : Context
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
        fun getInstance( weatherInterface: WeatherInterface,
                          localSource : LocalSource,
                          context : Context): Repository {
            Log.i("TAG", "inside repo get instance")
            return instance ?: Repository(
                weatherInterface, localSource, context
                /*WeatherService.getInstance(),*/
            )
        }
    }

    override val storedResponse: LiveData<List<WeatherModel>> = localSource.storedResponse

    override val allStoredFavorites: LiveData<List<FavoriteModel>> = localSource.allStoredFavorites

    override fun insertToFavorite(favoriteModel: FavoriteModel) {
        localSource.insertToFavorite(favoriteModel)
    }

    override fun deleteFromFavorite(favoriteModel: FavoriteModel) {
        localSource.deleteFromFavorite(favoriteModel)
    }

    override fun insertLastResponse(weatherModel: WeatherModel) {
        localSource.insertLastResponse(weatherModel)
    }

}