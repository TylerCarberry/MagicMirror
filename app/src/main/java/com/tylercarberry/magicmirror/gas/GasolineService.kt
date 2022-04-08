package com.tylercarberry.magicmirror.gas

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class GasolineService(
    private val client: OkHttpClient
) {

    companion object {
        private const val TAG = "GasolineService"
        private const val URL = "https://gasprices.aaa.com/"
    }

    fun getNationalGasPrice(): Double? {
        try {
            val request = Request.Builder()
                .url(URL)
                .build()

            val response = client.newCall(request).execute()
            val html = response.body?.string() ?: return null
            val document = Jsoup.parse(html)

            val price = document.getElementsByClass("numb")[0].text().replace("$", "")
            return price.toDoubleOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching gas price", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }
    }

}