package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteInteractor {
    fun favouriteTracks(): Flow<List<Track>>
    fun favouriteTracksID(): Flow<List<Int>>
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
}