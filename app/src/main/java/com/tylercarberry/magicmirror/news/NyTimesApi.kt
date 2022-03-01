package com.tylercarberry.magicmirror.news

import retrofit2.http.GET
import retrofit2.http.Query

interface NyTimesApi {

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
    }

    @GET("svc/topstories/v2/home.json")
    suspend fun getNews(
        @Query("api-key") apiKey: String
    ): NyTimes

}