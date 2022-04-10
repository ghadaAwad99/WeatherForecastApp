package com.example.weatherforcast.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.UserAlarm
import com.example.weatherforcast.model.WeatherModel

class ConcreteLocalSource(context: Context) : LocalSource {

    private val favDAO: FavDAO?
    override val allStoredFavorites: LiveData<List<FavoriteModel>>

    private val weatherDAO: WeatherDAO?
   // val storedResponse: WeatherModel

    private val alarmDAO: AlarmDAO?
    override val storedAlarms: LiveData<List<UserAlarm>>

    init {
        val database: AppDatabase = AppDatabase.getInstance(context)

        favDAO = database.favDAO()
        allStoredFavorites = favDAO.getAllFavorites()

        weatherDAO = database.weatherDAO()
       // storedResponse = weatherDAO!!.getLastResponse()

        alarmDAO = database.alarmDAO()
        storedAlarms = alarmDAO.getAllAlarms()
    }

    override suspend fun getLastResponseFromDB() : WeatherModel{
        Log.i("TAG", "inside getLastResponseFromDB concreate local source " + weatherDAO!!.getLastResponse())
        return  weatherDAO!!.getLastResponse()
    }

    override fun insertToFavorite(favoriteModel: FavoriteModel) {
        favDAO?.insertToFavorite(favoriteModel)
    }

    override fun deleteFromFavorite(favoriteModel: FavoriteModel) {
        favDAO?.deleteFromFavorite(favoriteModel)
    }

    override fun insertLastResponse(weatherModel: WeatherModel) {
        weatherDAO?.insertLastResponse(weatherModel)
    }

    override fun insertAlarm(userAlarm: UserAlarm) {
        alarmDAO?.insertAlarm(userAlarm)
    }


}