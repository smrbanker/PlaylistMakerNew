package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track

interface TracksRepository {
    fun searchTracks(text: String, text2: String): Resource<List<Track>>
}