package com.study.playlistmaker.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://itunes.apple.com"

    val itunesSearchApiService: ItunesSearchApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ItunesSearchApiService::class.java)
}
