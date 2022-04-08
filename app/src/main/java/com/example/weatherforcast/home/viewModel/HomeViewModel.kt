package com.example.weatherforcast.home.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.RepositoryInterface
import com.example.weatherforcast.model.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class HomeViewModel(var repository: RepositoryInterface): ViewModel() {

   private val weatherMutableLiveData : MutableLiveData<WeatherModel> = MutableLiveData()
    val weatherLiveData: LiveData<WeatherModel> = weatherMutableLiveData

    /*private val lastLocation : MutableLiveData<WeatherModel> = MutableLiveData()
    val lastLocationLiveData: LiveData<WeatherModel> = lastLocation*/

   fun getCurrTemp(lat : Double, lon : Double, key : String, language : String, unit : String,){
       Log.i("TAG", "inside getCurrTemp")
      viewModelScope.launch(Dispatchers.IO) {
          Log.i("TAG", "inside CoroutineScope")
          try {
              val response = repository.getCurrentWeather(lat, lon, key, language, unit)
              Log.i("TAG", "after response")
              withContext(Dispatchers.Main) {
                  Log.i("TAG", "inside withContext")
                  if (response.isSuccessful) {
                      Log.i("TAG", "inside is successful")
                      weatherMutableLiveData.postValue(response.body())
                      Log.d("HomeViewModel", "weatherMutableLiveData " + weatherMutableLiveData.value?.daily)
                  } else {
                      Log.e("HomeViewModel", "Error fetching data in HomeViewModel " + response.message())
                  }
              }
          }catch (ex : SocketTimeoutException){
              Log.e("TAG", ex.message.toString())

          }


      }
   }

    fun insertLastResponse(weatherModel: WeatherModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLastResponse(weatherModel)
        }
    }
    fun getLastResponseFromRoom(): LiveData<WeatherModel> {
        return repository.storedResponse
    }

}