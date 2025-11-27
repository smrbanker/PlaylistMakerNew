package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import com.practicum.playlistmaker.media.domain.db.FavouriteRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.TrackDbConvertor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavouriteRepository {

    override fun getFavouriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getFavouriteTracksID(): Flow<List<Int>> = flow {
        val tracks = appDatabase.trackDao().getTracksId()
        emit(tracks)
    }

    override suspend fun addFavouriteTrack(track: Track) {
        val tracksID = appDatabase.trackDao().getTracksId()
        if (track.trackId in tracksID) {
            appDatabase.trackDao().replaceTrackEntity(trackDbConvertor.map(track))
        } else {
            appDatabase.trackDao().insertTrackEntity(trackDbConvertor.map(track))
        }
    }

    override suspend fun deleteFavouriteTrack(track: Track) {
        appDatabase.trackDao().deleteTrackEntity(trackDbConvertor.map(track))
    }
    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}