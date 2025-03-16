package com.study.playlistmaker.search.data.network

import com.study.playlistmaker.search.data.dto.Response
import com.study.playlistmaker.search.data.dto.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val itunesSearchApiService: ItunesSearchApiService) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is SearchRequest) {
            withContext(Dispatchers.IO) {
                try {
                    val response = itunesSearchApiService.search(dto.query)
                    response.apply { resultCode = 200 }
                } catch (e: Exception) {
                    Response().apply { resultCode = 500 }
                }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}
