package com.example.weatherforcast.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherforcast.model.UserAlarm

@Dao
interface AlarmDAO {
    @Insert
    fun insertAlarm(userAlarm: UserAlarm)

    @Query("SELECT * FROM UserAlarms WHERE alarmTime BETWEEN startDate AND endDate")
    fun getAllAlarms() : List<UserAlarm>

    @Delete
    fun deleteAlarm(alarm: UserAlarm)
}