package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    val trackId: Int,
    val trackName: String,                                          // Название композиции
    val artistName: String,                                         // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: Int,  // Продолжительность трека
    val artworkUrl100: String,                                      // Ссылка на изображение обложки
    val collectionName: String,                                     // Название альбома
    val releaseDate: String,                                        // Год релиза
    val primaryGenreName: String,                                   // Жанр
    val country: String,                                            // Страна исполнителя
    val previewUrl: String,                                         // Ссылка на 30 сек отрывок
)