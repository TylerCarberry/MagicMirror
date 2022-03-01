package com.tylercarberry.magicmirror.weather

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * https://openweathermap.org/
 */
interface WeatherApi {

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/"

        const val IMPERIAL = "imperial"
        const val METRIC = "metric"
        const val ENGLISH = "en"
    }

    @GET("data/2.5/onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("apikey") apiKey: String,
        @Query("units") units: String = IMPERIAL,
        @Query("lang") language: String = ENGLISH
    ): WeatherResponse
}