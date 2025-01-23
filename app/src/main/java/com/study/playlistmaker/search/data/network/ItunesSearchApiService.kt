package com.study.playlistmaker.search.data.network

import com.study.playlistmaker.search.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesSearchApiService {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}
