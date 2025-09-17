package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel:ViewModel() {
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETE = 4
        const val DELAY = 200L
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel()
            }
        }
    }

    val playerInteractor = Creator.provideMediaPlayerInteractor()
    val mainThreadHandler = Handler(Looper.getMainLooper())

    private val playerState = MutableLiveData<Int>(STATE_DEFAULT)
    val state: LiveData<Int> get() = playerState

    private val infoState = MutableLiveData(Track())
    val info : LiveData<Track> get() = infoState

    fun resetInfo() {
        mainThreadHandler.removeCallbacksAndMessages(null)
        infoState.value = info.value?.copy(currentPosition = "00:00")
    }
    private fun stopCountTimer() {
        updateTime().let { mainThreadHandler.removeCallbacks(it) }
    }

    fun preparePlayer(url : String) {
        if (playerState.value == STATE_DEFAULT)
            playerInteractor.prepare(
                url,
                onPrepared = { playerState.value = STATE_PREPARED },
                onCompletion = { playerState.value = STATE_COMPLETE
                                resetInfo()
                                stopCountTimer()
                }
            )
    }

    private fun updateTime (): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerInteractor.isPlaying()) {
                    val formatTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentPosition())
                    infoState.value = info.value?.copy(currentPosition = formatTime)
                    mainThreadHandler.postDelayed(this, DELAY)
                }
            }
        }.also { mainThreadHandler.post(it) }
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerState.value = STATE_PAUSED
        stopCountTimer()
    }
    fun startPlayer() {
        playerInteractor.startPlayer()
        playerState.value = STATE_PLAYING
        mainThreadHandler.post(updateTime())
    }

    override fun onCleared() {
        super.onCleared()
        resetInfo()
    }
}