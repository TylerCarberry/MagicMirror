package com.tylercarberry.magicmirror.news

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tylercarberry.magicmirror.App
import com.tylercarberry.magicmirror.R

class NewsService(private val api: NyTimesApi) {

    suspend fun getFrontPageArticles(): List<Article> {
        return try {
            val apiKey = App.INSTANCE.getString(R.string.news_api_key)
            if (apiKey.isEmpty()) {
                Log.w("NewsService", "The news api key is missing. Add it to keys.xml")
                return listOf()
            }
            val response = api.getNews(apiKey)
            response.results
        } catch (exception: Exception) {
            FirebaseCrashlytics.getInstance().recordException(exception)
            Log.e("NewsService", "Error fetching news", exception)
            listOf()
        }
    }

}