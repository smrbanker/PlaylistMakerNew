package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun getDuration(playlistID : Int) : Int
    fun getTracks(playlistID: Int): Flow<List<Track>>
    suspend fun deleteTrack(trackID : Int, playlistID : Int)
    suspend fun getPlaylistCount(playlistID : Int) : Int
    suspend fun getPlaylistInfo(playlistID : Int) : String
    suspend fun deletePlaylist(playlistID : Int)
    suspend fun updatePlaylist(playlist: Playlist, playlistName : String, playlistDescription : String?, playlistUri : String?)
    suspend fun updatePlaylistView(playlistID : Int) : Playlist
}