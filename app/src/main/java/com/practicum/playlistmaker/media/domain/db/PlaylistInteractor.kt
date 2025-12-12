package com.practicum.playlistmaker.media.domain.db

import android.net.Uri
import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Int)
    fun getPlaylists(): Flow<List<Playlist>>

}