package com.study.playlistmaker.data

import com.study.playlistmaker.data.dto.Response

interface NetworkClient {

    fun doRequest(dto: Any): Response
}
