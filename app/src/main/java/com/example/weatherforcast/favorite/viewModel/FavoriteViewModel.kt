package com.example.weatherforcast.favorite.viewModel

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.RepositoryInterface
import com.example.weatherforcast.model.UserAlarm
import com.example.weatherforcast.model.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class FavoriteViewModel(private val repository: RepositoryInterface) : ViewModel() {

    private val weatherMutableLiveData : MutableLiveData<WeatherModel> = MutableLiveData()
    val weatherLiveData:LiveData<WeatherModel> = weatherMutableLiveData

    fun getCurrTemp(lat: Double, lon: Double, key: String, language: String, unit: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getCurrentWeather(lat, lon, key, language, unit)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        weatherMutableLiveData.value = response.body()
                        Log.d("FavoriteViewModel", "weatherMutableLiveData " + weatherMutableLiveData.value?.daily)
                    } else {
                        Log.e("FavoriteViewModel", "Error fetching data in FavoriteViewModel " + response.message())
                    }
                }
            }catch (ex : SocketTimeoutException){
                Log.e("TAG", ex.message.toString())
            }
        }
    }

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

}