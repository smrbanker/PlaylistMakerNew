package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getDuration(playlistID: Int): Int {
        return playlistsRepository.getDuration(playlistID)
    }

    override fun getTracks(playlistID: Int): Flow<List<Track>> {
        return playlistsRepository.getTracks(playlistID)
    }

    override suspend fun deleteTrack(trackID: Int, playlistID: Int) {
        playlistsRepository.deleteTrack(trackID, playlistID)
    }

    override suspend fun getPlaylistCount(playlistID: Int): Int {
        return playlistsRepository.getPlaylistCount(playlistID)
    }

    override suspend fun getPlaylistInfo(playlistID: Int): String {
        return playlistsRepository.getPlaylistInfo(playlistID)
    }

    override suspend fun deletePlaylist(playlistID: Int) {
        playlistsRepository.deletePlaylist(playlistID)
    }

    override suspend fun updatePlaylist(playlist: Playlist, playlistName : String, playlistDescription : String?, playlistUri : String?) {
        playlistsRepository.updatePlaylist(playlist, playlistName, playlistDescription, playlistUri)
    }

    override suspend fun updatePlaylistView(playlistID : Int) : Playlist {
        return playlistsRepository.updatePlaylistView(playlistID)
    }
}