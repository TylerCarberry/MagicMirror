package com.tylercarberry.magicmirror.inflation

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class InflationService(
    private val client: OkHttpClient
) {

    companion object {
        private const val URL = "https://www.bls.gov/cpi/latest-numbers.htm"
    }

    fun getInflationRate(): String? {
        val request = Request.Builder()
            .url(URL)
            .build()

        val response = client.newCall(request).execute()
        val html = response.body?.string() ?: return null
        val document = Jsoup.parse(html)

        val price = document.getElementsByClass("cpi")[4].text()
        return price.split(" ")[1].replace("+", "").trim()
    }

}