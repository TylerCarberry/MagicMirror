package com.tylercarberry.magicmirror.calendar

import android.util.Log
import biweekly.Biweekly
import biweekly.ICalendar
import biweekly.component.VEvent
import com.tylercarberry.magicmirror.App
import com.tylercarberry.magicmirror.MainViewModelImpl
import com.tylercarberry.magicmirror.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*

class CalendarService(
    private val client: OkHttpClient
) {

    fun updateCalendar(): List<VEvent> {
        val holidaysCal = getICalFromUrl(App.INSTANCE.getString(R.string.holidays_url))

        val calendarUrl = App.INSTANCE.getString(R.string.calendar_url)
        if (calendarUrl.isEmpty()) {
            Log.w("CalendarService", "The calendar url is missing. Add it to keys.xml")
        }
        val myCal = getICalFromUrl(calendarUrl)
        val now = Date()

        val holidays = holidaysCal?.events ?: listOf()
        val events = myCal?.events ?: listOf()

        return holidays
            .asSequence()
            .plus(events)
            .filter { it.dateEnd.value.after(now) }
            .filter { it.dateStart.value.time - 30 * MainViewModelImpl.SECONDS_IN_A_DAY < now.time }
            .sortedBy { it.dateStart.value }
            .take(5)
            .toList()
    }

    private fun getICalFromUrl(url: String): ICalendar? {
        val response = try {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute()
        } catch (e: IOException) {
            return null
        }

        val str = response.body?.string() ?: ""
        return Biweekly.parse(str).first()
    }

}