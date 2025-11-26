package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface MediaStateTrack {

    data class Content(
        val tracks: List<Track>
    ) : MediaStateTrack

    data class Empty(
        val message: String
    ) : MediaStateTrack
}