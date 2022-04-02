package com.example.weatherforcast.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherforcast.model.FavoriteModel

@Dao
interface FavDAO {
    @Insert
    fun insertToFavorite(favoriteModel: FavoriteModel)

    @Delete
    fun deleteFromFavorite(favoriteModel: FavoriteModel)

    @Query("SELECT * FROM favorite WHERE locality = :city")
    fun getFavoritePlace(city:String)
}