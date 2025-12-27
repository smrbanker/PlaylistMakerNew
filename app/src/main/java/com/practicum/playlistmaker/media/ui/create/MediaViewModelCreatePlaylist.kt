package com.practicum.playlistmaker.media.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.media.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.search.domain.Playlist
import kotlinx.coroutines.runBlocking

class MediaViewModelCreatePlaylist(private val playlistInteractor: PlaylistsInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<MediaStateCreate>()
    fun observeState(): LiveData<MediaStateCreate> = stateLiveData

    suspend fun insertPlaylist(playlistName : String, playlistDescription : String?, playlistUri : String?) {
        playlistInteractor.addPlaylist(
            Playlist(
                playlistId = 0,
                playlistName = playlistName,
                playlistDescription = playlistDescription,
                playlistImage = playlistUri,
                playlistList = "",
                playlistCount = 0
            )
        )
    }

    fun updatePlaylist(playlist: Playlist, playlistName : String, playlistDescription : String?, playlistUri : String?) {
        runBlocking { 
            playlistInteractor.updatePlaylist(playlist, playlistName, playlistDescription, playlistUri)
        }
    }

    fun completeData(playlist: Playlist) {
        val description = playlist.playlistDescription ?: ""
        val image = playlist.playlistImage ?: ""
        processResult(playlist.playlistName, description, image)
    }

    private fun processResult(playlistName : String, playlistDescription : String, playlistImage : String) {
        renderState(MediaStateCreate.Content(playlistName, playlistDescription, playlistImage))
    }

    private fun renderState(state: MediaStateCreate) {
        stateLiveData.postValue(state)
    }
}