package com.example.weatherforcast.alerts.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.RepositoryInterface
import com.example.weatherforcast.model.UserAlarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: RepositoryInterface) : ViewModel(){

    private val alarmsMutableLiveData : MutableLiveData<List<UserAlarm>> = MutableLiveData()
    val alarmsLiveData : LiveData<List<UserAlarm>> = alarmsMutableLiveData

    fun insertAlarm(userAlarm: UserAlarm){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlarm(userAlarm)
        }
    }

    fun getAllAlarms() : LiveData<List<UserAlarm>> {
        return repository.allStoredAlarms
    }

    

}