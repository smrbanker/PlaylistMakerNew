package com.practicum.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId : Int,
    val playlistName : String,
    val playlistDescription : String?,
    val playlistImage : String?,
    val playlistList: String?,
    val playlistCount : Int
)