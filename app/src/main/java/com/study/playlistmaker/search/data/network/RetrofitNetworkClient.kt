package com.study.playlistmaker.search.data.network

import com.study.playlistmaker.search.data.dto.Response
import com.study.playlistmaker.search.data.dto.SearchRequest

class RetrofitNetworkClient(private val itunesSearchApiService: ItunesSearchApiService) : NetworkClient {

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
