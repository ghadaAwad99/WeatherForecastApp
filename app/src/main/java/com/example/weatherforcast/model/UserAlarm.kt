package com.example.weatherforcast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserAlarms")
data class UserAlarm (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val startDate : Long,
    val endDate : Long,
    val alarmTime : Long
        )