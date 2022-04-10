package com.example.weatherforcast.model

data class Alerts (
    val senderName: String,
    val event : String,
    val start: Double,
    val end: Double,
    val description:String,
    val tags:List<String>
        )