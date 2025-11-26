package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun getFavouriteTracks(): Flow<List<Track>>
    fun getFavouriteTracksID(): Flow<List<Int>>
    suspend fun addFavouriteTrack(track: Track)
    suspend fun deleteFavouriteTrack(track: Track)
}