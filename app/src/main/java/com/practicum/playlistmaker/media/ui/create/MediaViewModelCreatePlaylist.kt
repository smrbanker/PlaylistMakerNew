package com.practicum.playlistmaker.media.ui.create

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.media.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.search.domain.Playlist

class MediaViewModelCreatePlaylist(private val playlistInteractor: PlaylistsInteractor): ViewModel() {

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

}