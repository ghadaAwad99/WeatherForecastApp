package com.example.weatherforcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.WeatherModel

@Database(entities = [FavoriteModel::class , WeatherModel::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun weatherDAO() : FavDAO

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

//Two tables: one for favorite and one for the last response to be shown when offline

