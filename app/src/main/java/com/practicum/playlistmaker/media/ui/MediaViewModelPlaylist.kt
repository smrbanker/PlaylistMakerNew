package com.practicum.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaViewModelPlaylist (playlist : String) : ViewModel() {

    private val playlistLiveData = MutableLiveData(playlist)
    fun observePlaylist(): LiveData<String> = playlistLiveData
}