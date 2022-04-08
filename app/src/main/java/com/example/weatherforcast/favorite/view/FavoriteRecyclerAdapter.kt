package com.example.weatherforcast.favorite.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.FavoriteItemBinding
import com.example.weatherforcast.model.FavoriteModel
import com.google.android.material.snackbar.Snackbar

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
        holder.binding.cardView.setOnClickListener {
            if (isOnline(holder.binding.cardView.context)) {listener.onDisplayClick(favItem)}
        else{
            Snackbar.make(holder.binding.cardView, holder.binding.cardView.context.getString(R.string.you_are_offline), Snackbar.LENGTH_LONG ).show()
        }}
    }

    override fun getItemCount() = favoriteList.size
}

class FavViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)


fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}