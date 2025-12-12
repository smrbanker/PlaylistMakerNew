package com.practicum.playlistmaker.media.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.media.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistsRepository.deletePlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }
}