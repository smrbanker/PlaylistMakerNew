package com.practicum.playlistmaker.media.ui.track

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch

class MediaViewModelTrack(
    private val context: Context,
    private val favouriteInteractor: FavouriteInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<MediaStateTrack>()
    fun observeState(): LiveData<MediaStateTrack> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            favouriteInteractor
                .favouriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(MediaStateTrack.Empty(context.getString(R.string.empty_media)))
        } else {
            renderState(MediaStateTrack.Content(tracks))
        }
    }

    private fun renderState(state: MediaStateTrack) {
        stateLiveData.postValue(state)
    }
}