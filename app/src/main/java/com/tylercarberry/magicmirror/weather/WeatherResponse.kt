package com.tylercarberry.magicmirror.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezoneOffset: Int,
    val current: Current,
    val minutely: List<Minutely>,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
    val alerts: List<Alert>
)

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Double,
    val humidity: Double,
    val dewPoint: Double,
    @SerializedName("uvi") val uvIndex: Double,
    @SerializedName("clouds") val cloudinessPercent: Double,
    val visibility: Double,
    val windSpeed: Double,
    val windDeg: Double,
    val windGust: Double,
    //val rain: Double,
    //val snow: Double,
    val weather: List<Weather>
)

data class Weather(
    val id: String,
    val main: String,
    val description: String,
    val icon: String
)

data class Minutely (
    val dt: Long,
    val precipitation: Double
)

data class Hourly (
    val dt: Long,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Double,
    val humidity: Double,
    val dewPoint: Double,
    @SerializedName("uvi") val uvIndex: Double,
    @SerializedName("clouds") val cloudinessPercent: Double,
    val visibility: Double,
    val windSpeed: Double,
    val windDeg: Double,
    val windGust: Double,
    //val rain: Double,
    //val snow: Double,
    val weather: List<Weather>
)

data class Daily (
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: Double,
    val temp: DailyTemp,
    val feelsLike: FeelsLikeTemp,
    val pressure: Double,
    val humidity: Double,
    val dewPoint: Double,
    @SerializedName("uvi") val uvIndex: Double,
    @SerializedName("clouds") val cloudinessPercent: Double,
    val visibility: Double,
    val windSpeed: Double,
    val windDeg: Double,
    val windGust: Double,
    @SerializedName("pop") val probabilityOfPrecipitation: Double,
    //val rain: Double,
    //val snow: Double,
    val weather: List<Weather>
)

data class DailyTemp (
    @SerializedName("morn") val morning: Double,
    val day: Double,
    @SerializedName("eve") val evening: Double,
    val night: Double,
    val min: Double,
    val max: Double
)

data class FeelsLikeTemp(
    @SerializedName("morn") val morning: Double,
    val day: Double,
    @SerializedName("eve") val evening: Double,
    val night: Double
)

data class Alert(
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)