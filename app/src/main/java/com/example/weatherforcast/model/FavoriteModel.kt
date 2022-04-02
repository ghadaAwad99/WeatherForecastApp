package com.example.weatherforcast.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteModel(
    @PrimaryKey
    @ColumnInfo(name = "locality")
    val locality:String,
    @ColumnInfo(name = "lat")
    val lat:Double,
    @ColumnInfo(name = "lon")
    val lon: Double
)