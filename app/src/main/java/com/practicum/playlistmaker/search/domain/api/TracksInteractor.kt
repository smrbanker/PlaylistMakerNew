package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track

interface TracksInteractor {
    fun searchTracks(text: String, text2: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}