package com.practicum.playlistmaker.media.ui.create

sealed interface MediaStateCreate {

    data class Content(
        val playlistName : String,
        val playlistDescription : String,
        val playlistImage : String
    ) : MediaStateCreate

}