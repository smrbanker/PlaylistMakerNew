package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.media.domain.db.FavouriteRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteInteractorImpl(
    private val favouriteRepository: FavouriteRepository
) : FavouriteInteractor {

    override fun favouriteTracks(): Flow<List<Track>> {
        return favouriteRepository.getFavouriteTracks().map {tracks -> tracks.reversed()}
    }

    override fun favouriteTracksID(): Flow<List<Int>> {
        return favouriteRepository.getFavouriteTracksID()
    }

    override suspend fun addTrack(track: Track) {
        favouriteRepository.addFavouriteTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        favouriteRepository.deleteFavouriteTrack(track)
    }
}