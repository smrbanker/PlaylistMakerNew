package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.iTinesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesAPI {
    @GET("/search")
    suspend fun search(@Query("term") text: String, @Query("entity") text2: String) : iTinesResponse
}