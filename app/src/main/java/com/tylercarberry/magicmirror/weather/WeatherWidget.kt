package com.tylercarberry.magicmirror.weather

data class WeatherWidget(
    val currentDegrees: Int,
    val icon: String?,
    val daily: List<Daily>
)