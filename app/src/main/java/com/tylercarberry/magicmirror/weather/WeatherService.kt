package com.tylercarberry.magicmirror.weather

import android.location.Location
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tylercarberry.magicmirror.App
import com.tylercarberry.magicmirror.R

class WeatherService(
    private val weatherApi: WeatherApi
) {

    @WorkerThread
    suspend fun updateWeather(location: Location): WeatherResponse? {
        return try {
             return weatherApi.getWeather(location.latitude, location.longitude, App.INSTANCE.getString(R.string.weather_api_key))
        } catch (exception: Exception) {
            Log.e("WeatherService", "Error fetching weather", exception)
            FirebaseCrashlytics.getInstance().recordException(exception)
            null
        }
    }

}