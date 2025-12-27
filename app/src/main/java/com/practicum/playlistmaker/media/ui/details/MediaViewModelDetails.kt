package com.practicum.playlistmaker.media.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MediaViewModelDetails(
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor : SharingInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<MediaStateDetails>()
    fun observeState(): LiveData<MediaStateDetails> = stateLiveData

    private val stateLiveData2 = MutableLiveData<MediaStateDetails>()
    fun observeState2(): LiveData<MediaStateDetails> = stateLiveData2

    private val stateLiveData3 = MutableLiveData<MediaStateDetails>()
    fun observeState3(): LiveData<MediaStateDetails> = stateLiveData3

    private val stateLiveData4 = MutableLiveData<MediaStateDetails>()
    fun observeState4(): LiveData<MediaStateDetails> = stateLiveData4

    fun getDuration(id: Int) {
        viewModelScope.launch {
            processResult(playlistsInteractor.getDuration(id))
        }
    }

    fun fillData(id: Int) {
        viewModelScope.launch {
            playlistsInteractor
                .getTracks(id)
                .collect { tracks -> processResultTracks(tracks) }
        }
    }

    fun deleteTrack(id: Int, id2: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrack(id, id2)
            processResult(playlistsInteractor.getDuration(id2))
            processResultPlaylist(playlistsInteractor.getPlaylistCount(id2))
        }
    }

    fun shareInfo(id: Int) {
        var string = ""
        runBlocking {
            string = playlistsInteractor.getPlaylistInfo(id)
        }
        sharingInteractor.sharePlaylist(string)
    }

    fun deletePlaylist(id: Int) {
        runBlocking {
            playlistsInteractor.deletePlaylist(id)
        }
    }

    fun updatePlaylistView(id: Int) {
        viewModelScope.launch {
            processResultView(playlistsInteractor.updatePlaylistView(id))
        }
    }

    private fun processResult(long : Int) {
        renderState(MediaStateDetails.Content(long))
    }

    private fun processResultTracks(tracks : List<Track>) {
        renderState2(MediaStateDetails.ContentTracks(tracks))
    }

    private fun processResultPlaylist(count : Int) {
        renderState3(MediaStateDetails.ContentPlaylist(count))
    }

    private fun processResultView(playlist: Playlist) {
        renderState4(MediaStateDetails.ContentPlaylistView(playlist))
    }

    private fun renderState(state: MediaStateDetails) {
        stateLiveData.postValue(state)
    }

    private fun renderState2(state: MediaStateDetails) {
        stateLiveData2.postValue(state)
    }

    private fun renderState3(state: MediaStateDetails) {
        stateLiveData3.postValue(state)
    }

    private fun renderState4(state: MediaStateDetails) {
        stateLiveData4.postValue(state)
    }
}