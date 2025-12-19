package com.practicum.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracklist")
data class TrackListEntity(
    @PrimaryKey
    val trackId: Int,
    val trackName: String,                                          // Название композиции
    val artistName: String,                                         // Имя исполнителя
    val trackTime: Int,                                             // Продолжительность трека
    val artworkUrl100: String,                                      // Ссылка на изображение обложки
    val collectionName: String,                                     // Название альбома
    val releaseDate: String,                                        // Год релиза
    val primaryGenreName: String,                                   // Жанр
    val country: String,                                            // Страна исполнителя
    val previewUrl: String,                                         // Ссылка на 30 сек отрывок
    val timeStamp: Long = System.currentTimeMillis()                // Системное время добавления трека
)