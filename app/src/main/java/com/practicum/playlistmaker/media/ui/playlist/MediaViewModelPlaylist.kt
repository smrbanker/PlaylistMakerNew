package com.practicum.playlistmaker.media.ui.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.search.domain.Playlist
import kotlinx.coroutines.launch

class MediaViewModelPlaylist(
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<MediaStatePlaylist>()
    fun observeState(): LiveData<MediaStatePlaylist> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(MediaStatePlaylist.Empty(context.getString(R.string.empty_media)))
        } else {
            renderState(MediaStatePlaylist.Content(playlists))
        }
    }

    private fun renderState(state: MediaStatePlaylist) {
        stateLiveData.postValue(state)
    }
}