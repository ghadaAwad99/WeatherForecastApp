package com.example.weatherforcast.favorite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.databinding.FavoriteItemBinding
import com.example.weatherforcast.model.FavoriteModel

class FavoriteRecyclerAdapter(private val listener: OnClickListener) :
    RecyclerView.Adapter<FavViewHolder>() {


    //get list from ROOM
    private var favoriteList = mutableListOf<FavoriteModel>()

    fun setFavoriteList(favoriteList: List<FavoriteModel>) {
        this.favoriteList = favoriteList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavoriteItemBinding.inflate(inflater, parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val favItem = favoriteList[position]

        holder.binding.localityText.text = favItem.locality
        holder.binding.deleteButton.setOnClickListener { listener.onDeleteClick(favItem) }
        holder.binding.cardView.setOnClickListener { listener.onDisplayClick(favItem) }
    }

    override fun getItemCount() = favoriteList.size
}

class FavViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)