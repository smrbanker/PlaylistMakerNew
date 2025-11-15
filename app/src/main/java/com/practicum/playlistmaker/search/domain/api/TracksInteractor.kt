package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(text: String, text2: String): Flow<Pair<List<Track>?, String?>>
}