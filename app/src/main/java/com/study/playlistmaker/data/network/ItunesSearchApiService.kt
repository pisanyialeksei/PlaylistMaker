package com.study.playlistmaker.data.network

import com.study.playlistmaker.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApiService {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}
