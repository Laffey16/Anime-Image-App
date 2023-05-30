package com.example.animeapp.requests

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object GelbooruSingleton {
    val api: ApiInterface = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)
}
