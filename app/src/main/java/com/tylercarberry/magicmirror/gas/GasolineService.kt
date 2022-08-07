package com.tylercarberry.magicmirror.gas

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class GasolineService(
    private val client: OkHttpClient
) {

    companion object {
        private const val TAG = "GasolineService"

        private const val NATIONAL_URL = "https://gasprices.aaa.com"
        private const val MASSACHUSETTS_URL = "https://gasprices.aaa.com/?state=MA"
    }

    fun getBostonGasPrice(): Double? {
        try {
            val document = getDocument(MASSACHUSETTS_URL) ?: return null

            val price = document.getElementsContainingOwnText("Boston")[0].attr("data-cost").replace("$", "")
            return price.toDoubleOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching boston gas price", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }
    }

    fun getMassachusettsGasPrice(): Double? {
        try {
            val document = getDocument(MASSACHUSETTS_URL) ?: return null

            val price = document.getElementsByClass("numb")[1].text().replace("$", "")
            return price.toDoubleOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching massachusetts gas price", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }
    }

    fun getNationalGasPrice(): Double? {
        try {
            val document = getDocument(NATIONAL_URL) ?: return null

            val price = document.getElementsByClass("numb")[0].text().replace("$", "")
            return price.toDoubleOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching national gas price", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }
    }

    private fun getDocument(url: String): Document? {
        try {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val html = response.body?.string() ?: return null
            return Jsoup.parse(html)
        } catch (e: IOException) {
            return null
        }
    }

}