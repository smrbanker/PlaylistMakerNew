package com.practicum.playlistmaker.media.ui.playlist

import com.practicum.playlistmaker.search.domain.Playlist

sealed interface MediaStatePlaylist {

    data class Content(
        val playlist: List<Playlist>
    ) : MediaStatePlaylist

    data class Empty(
        val message: String
    ) : MediaStatePlaylist
}