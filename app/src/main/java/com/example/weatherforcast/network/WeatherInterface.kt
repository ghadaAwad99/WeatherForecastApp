package com.example.weatherforcast.network

import com.example.weatherforcast.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    //@GET("data/2.5/onecall?lat=31.200092&lon=29.918739&appid=8bdc89e28e3ae5c674e20f1d16e70f7d")
    @GET("onecall")
    suspend fun getCurrentTemp(@Query("lat")lat : Double,
                               @Query("lon")lon : Double,
                               @Query("appid")key : String,
                               @Query("lang")language : String ,
                               @Query("lon")unit : Double = 0.0,
                               ) : Response<WeatherModel>


/*    companion object {
        var retrofitService: WeatherInterface? = null

        fun getInstance() : WeatherInterface {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.androidhive.info/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(WeatherInterface::class.java)
            }
            return retrofitService!!
        }
    }*/
}


//https://api.openweathermap.org/data/2.5/onecall?lat=31.200092&lon=29.918739&appid=8bdc89e28e3ae5c674e20f1d16e70f7d
//https://api.androidhive.info/json/movies.json