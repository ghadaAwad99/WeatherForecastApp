package com.example.weatherforcast.favorite.view

import com.example.weatherforcast.model.FavoriteModel

interface OnClickListener {
    fun onDeleteClick(favoriteModel: FavoriteModel)
    fun onDisplayClick(favoriteModel: FavoriteModel)
}