package com.example.weatherforcast.home.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.R
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.model.RepositoryInterface
import com.example.weatherforcast.model.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class HomeViewModel(var repository: RepositoryInterface, var context: Context) : ViewModel() {

    private val weatherMutableLiveData: MutableLiveData<WeatherModel> = MutableLiveData()
    val weatherLiveData: LiveData<WeatherModel> = weatherMutableLiveData

    fun getCurrTemp(lat: Double, lon: Double, key: String, language: String, unit: String) {
        Log.i("TAG", "inside getCurrTemp")
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "inside CoroutineScope")
            try {
                val response = repository.getCurrentWeather(lat, lon, key, language, unit)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        weatherMutableLiveData.postValue(response.body())
                    } else {
                        Log.e("HomeViewModel", "Error fetching data in HomeViewModel ${response.message()}" )
                    }
                }
            } catch (ex: SocketTimeoutException) {
                Log.e("TAG", ex.message.toString())
            }
        }
    }

    fun getCurrentWeatherResponse(lat: Double, lon: Double, lang: String) {
        if (!Utilities.isOnline(context)) {
            Toast.makeText(context, context.getString(R.string.you_are_offline), Toast.LENGTH_LONG)
                .show()
            getLastResponseFromRoom()
        } else {
            getCurrTemp(lat, lon, Utilities.ApiKey, lang, "metric")
        }
    }


    fun insertLastResponse(weatherModel: WeatherModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLastResponse(weatherModel)
        }
    }

    fun getLastResponseFromRoom() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                weatherMutableLiveData.postValue(repository.getLastResponseFromDB())
            }
        }
}