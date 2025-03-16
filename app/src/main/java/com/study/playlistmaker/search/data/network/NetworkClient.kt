package com.study.playlistmaker.search.data.network

import com.study.playlistmaker.search.data.dto.Response

interface NetworkClient {

    suspend fun doRequest(dto: Any): Response
}
