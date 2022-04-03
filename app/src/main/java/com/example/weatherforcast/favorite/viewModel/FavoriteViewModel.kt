package com.example.weatherforcast.favorite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: RepositoryInterface) : ViewModel() {

    fun deleteFavorite(favoriteModel: FavoriteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFromFavorite(favoriteModel)
        }
    }

    fun getAllFavorite(): LiveData<List<FavoriteModel>> {
        return repository.allStoredFavorites
    }
}