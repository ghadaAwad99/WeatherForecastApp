package com.example.weatherforcast.alerts.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherforcast.home.view.HomeScreen
import com.example.weatherforcast.model.UserAlarm
import android.R
import android.app.Notification
import android.content.ContentResolver
import android.content.SharedPreferences
import android.net.Uri
import android.media.AudioAttributes
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.example.weatherforcast.Utilities
import com.example.weatherforcast.alerts.viewModel.AlertsViewModel
import com.example.weatherforcast.database.ConcreteLocalSource
import com.example.weatherforcast.model.Repository
import com.example.weatherforcast.model.WeatherModel
import com.example.weatherforcast.network.WeatherInterface
import com.example.weatherforcast.network.WeatherService


class AlertsWorkManger (private val context : Context, private val params: WorkerParameters):
    CoroutineWorker(context,params) {

    //lateinit var alarmsList : List<UserAlarm>
    lateinit var repository : Repository
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var lang: String
    lateinit var choosenLocation: String
    var lat: Float = 0f
    var lon: Float = 0f



    override suspend fun doWork(): Result {
        sharedPreferences = context.getSharedPreferences(context.getString(com.example.weatherforcast.R.string.shared_prefs),
            AppCompatActivity.MODE_PRIVATE
        )
        repository= Repository.getInstance(WeatherService.getInstance(), ConcreteLocalSource(context), context)
        lang = sharedPreferences.getString("LANGUAGE", "en").toString()
        //get lat and long from last stored response in ROOM
       var currentWeatherResponse : WeatherModel =  repository.getLastResponseFromDB()
       var lat =  currentWeatherResponse.lat
       var lon =  currentWeatherResponse.lon
        Log.i("TAG", "Inside do work lat is $lat and long is $lon")

        //send request to check for alerts in this location
        var response = repository.getCurrentWeather(lat, lon, Utilities.ApiKey, lang, "metric")
            if (response.isSuccessful){
                if (response.body()?.alerts?.get(0)?.event.isNullOrBlank()) {
                    displayNotification("There is no alerts for this time")
                    //AlertsViewModel.findNextAlarm()
                }else{
                    displayNotification(" ${response.body()?.alerts?.get(0)?.event.toString()} " +
                            ".Be Careful! ")
                    //AlertsViewModel.findNextAlarm()
                }
            }else{
                Log.e("WorkManager", "Error fetching data in HomeViewModel " + response.message())
            }


        return Result.success()
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun displayNotification(keyword: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT)
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, HomeScreen::class.java)
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, "simplifiedcoding")
            .setContentTitle("Cloudy Reminder")
            .setContentText(keyword)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + context.packageName + "/raw/alert_sound")
               // Uri.parse("android.resource://" + context.packageName + "/" + com.example.weatherforcast.R.raw.alert_sound)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(com.example.weatherforcast.R.drawable.sun_notif)
        assert(notificationManager != null)
            notificationManager.notify(1, builder.build())
    }

}


