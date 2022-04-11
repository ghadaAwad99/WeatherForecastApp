package com.example.weatherforcast.alerts.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.view.AlertsWorkManger
import com.example.weatherforcast.model.RepositoryInterface
import com.example.weatherforcast.model.UserAlarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.Util
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class AlertsViewModel(private val repository: RepositoryInterface) : ViewModel(){

    private val alarmsMutableLiveData : MutableLiveData<List<UserAlarm>> = MutableLiveData()
    val alarmsLiveData : LiveData<List<UserAlarm>> = alarmsMutableLiveData

    fun insertAlarm(userAlarm: UserAlarm){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlarm(userAlarm)
        }
    }

    fun getAllAlarms()  =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                alarmsMutableLiveData.postValue(repository.getAllStoredAlarms())
            }
        }

    fun deleteAlarm(alarm: UserAlarm){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlarm(alarm)
        }
    }
    }





