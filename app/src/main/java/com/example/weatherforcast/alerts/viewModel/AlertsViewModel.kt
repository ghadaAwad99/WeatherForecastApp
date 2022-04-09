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
import okhttp3.internal.Util
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class AlertsViewModel(private val repository: RepositoryInterface) : ViewModel(){

    lateinit var alarmsList : List<UserAlarm>
    /*private val alarmsMutableLiveData : MutableLiveData<List<UserAlarm>> = MutableLiveData()
    val alarmsLiveData : LiveData<List<UserAlarm>> = alarmsMutableLiveData*/

    fun insertAlarm(userAlarm: UserAlarm){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlarm(userAlarm)
        }
    }

    fun getAllAlarms() : LiveData<List<UserAlarm>> {
        return repository.allStoredAlarms
    }

    fun setAlarmsListForManager(alarmsList : List<UserAlarm>){
        this.alarmsList = alarmsList
    }


    companion object {
        @RequiresApi(api = Build.VERSION_CODES.O)
        fun findNextAlarm(alarmsList: List<UserAlarm>) {
            WorkManager.getInstance().cancelAllWorkByTag("alarms")
            val currentTime = Calendar.getInstance().timeInMillis
            Log.i("TAG", "current time$currentTime")
            var smallest = currentTime
            var scheduledAlarm: String? = null
            var timeInMills: Long = 0
            for (alarm in alarmsList) {
                Log.i("TAG", " ")
                Log.i("TAG", " ")
                timeInMills = alarm.alarmTime
                Log.i(
                    "TAG",
                    "In side findRestMills " + timeInMills + " " + (timeInMills - currentTime)
                )
                Log.i("TAG", "current time$currentTime")
                if (timeInMills - currentTime >= 0 && timeInMills - currentTime < smallest) {
                    smallest = timeInMills - currentTime
                    scheduledAlarm = timeInMills.toString()
                    Log.i("TAG", "FinfResut If $scheduledAlarm")

                }
            }
            if (scheduledAlarm != null) {
                val currentTime = Calendar.getInstance().timeInMillis
                Log.i("TAG", "In side smallest reminder method")
                val finalTime = timeInMills - currentTime
                val data = Data.Builder()
                    .build()
                val reminderRequest = OneTimeWorkRequest.Builder(AlertsWorkManger::class.java)
                    .setInitialDelay(finalTime, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag("alarms")
                    .build()
                WorkManager.getInstance().enqueue(reminderRequest)
            }

        }
    }

}