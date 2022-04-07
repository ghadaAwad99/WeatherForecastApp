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
import com.example.weatherforcast.R
import com.example.weatherforcast.home.view.HomeScreen

class AlertsWorkManger (private val context : Context, private val params: WorkerParameters):
    CoroutineWorker(context,params) {

    override suspend fun doWork(): Result {
        displayNotification("WEATHER NOTIFICATION")
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

        val pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, "simplifiedcoding")
            .setContentTitle("Alert Reminder $keyword")
            .setContentText("We Care About You!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.sun_notif)
        assert(notificationManager != null)
            notificationManager.notify(1, builder.build())
    }
}
