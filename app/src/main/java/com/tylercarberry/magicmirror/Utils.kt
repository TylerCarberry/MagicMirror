package com.tylercarberry.magicmirror

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Utils {

    const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    const val DAY_OF_WEEK_PATTERN = "EEEE"

    fun hasLocationPermission(context: Context): Boolean {
        return hasPermission(context, LOCATION_PERMISSION)
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    fun Int.asOrdinal(): String {
         return "$this" + if (this % 100 in 11..13) "th" else when (this % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}