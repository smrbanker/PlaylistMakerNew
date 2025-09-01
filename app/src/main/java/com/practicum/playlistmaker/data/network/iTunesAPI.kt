package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.iTinesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesAPI {
    @GET("/search")
    fun search(@Query("term") text: String, @Query("entity") text2: String) : Call<iTinesResponse>
}