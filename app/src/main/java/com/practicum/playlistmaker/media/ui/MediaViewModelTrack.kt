package com.practicum.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaViewModelTrack (track : String) : ViewModel() {

    private val trackLiveData = MutableLiveData(track)
    fun observeTrack(): LiveData<String> = trackLiveData
}