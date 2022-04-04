package com.example.weatherforcast.favorite.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcast.model.RepositoryInterface

class FavoriteViewModelFactory(private val _repo: RepositoryInterface, private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(_repo, context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}

