package com.example.animeapp.requests

import com.example.animeapp.gelbooru.GelBooruData
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://gelbooru.com/"

interface ApiInterface {
    @GET("index.php?page=dapi&s=post&q=index&json=1&limit=1")
    suspend fun getData(@Query("tags") tags: String): GelBooruData
}
