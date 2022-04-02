package com.example.weatherforcast.database

import androidx.room.Dao
import androidx.room.Insert
import com.example.weatherforcast.model.FavoriteModel

@Dao
interface FavDAO {
    @Insert
    fun insertToFavorite(favoriteModel: FavoriteModel)
}