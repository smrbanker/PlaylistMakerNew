package com.practicum.playlistmaker.media.ui.details

import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track

sealed interface MediaStateDetails {

    data class Content(val long : Int) : MediaStateDetails
    data class ContentTracks(val tracks : List<Track>) : MediaStateDetails
    data class ContentPlaylist(val count: Int) : MediaStateDetails
    data class ContentPlaylistView(val playlist: Playlist) : MediaStateDetails
}