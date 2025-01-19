package com.study.playlistmaker.search.data.network

import com.study.playlistmaker.search.data.dto.Response
import com.study.playlistmaker.search.data.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesSearchApiService: ItunesSearchApiService = retrofit.create(ItunesSearchApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is SearchRequest) {
            try {
                val response = itunesSearchApiService.search(dto.query).execute()
                val body = response.body() ?: Response()
                return body.apply { resultCode = response.code() }
            } catch (e: Exception) {
                return Response().apply { resultCode = 500 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}
