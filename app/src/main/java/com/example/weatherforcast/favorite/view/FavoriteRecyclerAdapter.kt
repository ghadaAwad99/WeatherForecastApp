package com.example.weatherforcast.favorite.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.databinding.FavoriteItemBinding
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.Hourly

class FavoriteRecyclerAdapter : RecyclerView.Adapter<FavViewHolder>() {

    //get list from ROOM
    private var favoriteList = mutableListOf<FavoriteModel>()

    fun setFavoriteList(favoriteList: List<FavoriteModel>) {
        this.favoriteList = favoriteList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = favoriteList.size
}

class FavViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)