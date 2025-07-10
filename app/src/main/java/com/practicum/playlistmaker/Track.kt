package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackId: Int,
    val trackName: String,                                          // Название композиции
    val artistName: String,                                         // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: Int,  // Продолжительность трека
    val artworkUrl100: String,                                      // Ссылка на изображение обложки
)
