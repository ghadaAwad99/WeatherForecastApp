package com.example.weatherforcast.alerts.view

import com.example.weatherforcast.model.FavoriteModel
import com.example.weatherforcast.model.UserAlarm

interface onDeleteListener {
    fun onDeleteClick(alert: UserAlarm)
}