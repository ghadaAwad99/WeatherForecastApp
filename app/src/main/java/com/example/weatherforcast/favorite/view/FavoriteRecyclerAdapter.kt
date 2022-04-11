package com.example.weatherforcast.favorite.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.databinding.FavoriteItemBinding
import com.example.weatherforcast.model.FavoriteModel
import com.google.android.material.snackbar.Snackbar

class FavoriteRecyclerAdapter(private val listener: OnClickListener) :
    RecyclerView.Adapter<FavViewHolder>() {

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
        holder.binding.cardView.setOnClickListener {
            if (Utilities.isOnline(holder.binding.cardView.context)) {listener.onDisplayClick(favItem)}
        else{
            Snackbar.make(holder.binding.cardView, holder.binding.cardView.context.getString(R.string.you_are_offline), Snackbar.LENGTH_LONG ).show()
        }}
    }

    override fun getItemCount() = favoriteList.size
}

class FavViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)

