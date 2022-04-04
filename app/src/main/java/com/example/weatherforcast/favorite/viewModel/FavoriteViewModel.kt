package com.example.weatherforcast.favorite.viewModel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.R
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: RepositoryInterface, private val context: Context) : ViewModel() {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var lang:String

    fun deleteFavorite(favoriteModel: FavoriteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFromFavorite(favoriteModel)
        }
    }

    fun getAllFavorite(): LiveData<List<FavoriteModel>> {
        return repository.allStoredFavorites
    }

    fun insertFavorite(favoriteModel: FavoriteModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertToFavorite(favoriteModel)
        }
    }

    fun displayFavorite(favoriteModel: FavoriteModel){
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_prefs),
            AppCompatActivity.MODE_PRIVATE
        )
        lang = sharedPreferences.getString("LANGUAGE", "en").toString()
        var favIntent = Intent(context, HomeScreen::class.java)
        favIntent.putExtra("FAV LAT", favoriteModel.lat)
        favIntent.putExtra("FAV LON", favoriteModel.lon)
        favIntent.putExtra("FAV LANG", lang)
        favIntent.putExtra("FAV LOCALITY",favoriteModel.locality )
        context.startActivity(favIntent)


       /* viewModelScope.launch(Dispatchers.IO){
            repository.getCurrentWeather(favoriteModel.lat, favoriteModel.lon, lang)
        }*/
    }
}