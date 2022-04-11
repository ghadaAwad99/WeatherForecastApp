package com.example.weatherforcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.UserAlarm
import com.example.weatherforcast.model.WeatherModel

@Database(entities = [FavoriteModel::class , WeatherModel::class, UserAlarm::class], version = 7)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun weatherDAO() : WeatherDAO
    abstract fun favDAO() : FavDAO
    abstract fun alarmDAO() : AlarmDAO

    companion object{
        private var instance : AppDatabase? =null
        @Synchronized
        fun getInstance(context: Context) : AppDatabase{
            return instance?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "WeatherDatabase"
            ).fallbackToDestructiveMigration().build()
        }
    }
}


