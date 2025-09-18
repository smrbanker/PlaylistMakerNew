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
    private var formatTime = "00:00"

    val playerStateInfo = MutableLiveData(PlayerStateInfo(STATE_DEFAULT, "00:00"))
    fun observePlayerStateInfo(): LiveData<PlayerStateInfo> = playerStateInfo

    fun resetInfo() {
        mainThreadHandler.removeCallbacksAndMessages(null)
    }
    private fun stopCountTimer() {
        updateTime().let { mainThreadHandler.removeCallbacks(it) }
    }

    fun preparePlayer(url : String) {
        if (playerStateInfo.value?.state == STATE_DEFAULT)
            playerInteractor.prepare(
                url,
                onPrepared = { playerStateInfo.postValue(PlayerStateInfo(STATE_PREPARED, formatTime))},
                onCompletion = {
                                playerStateInfo.postValue(PlayerStateInfo(STATE_COMPLETE, "00:00"))
                                resetInfo()
                                stopCountTimer()
                }
            )
    }

    private fun updateTime (): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerInteractor.isPlaying()) {
                    formatTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentPosition())
                    playerStateInfo.postValue(PlayerStateInfo(STATE_PLAYING, formatTime))
                    mainThreadHandler.postDelayed(this, DELAY)
                }
            }
        }.also { mainThreadHandler.post(it) }
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerStateInfo.postValue(PlayerStateInfo(STATE_PAUSED, formatTime))
        stopCountTimer()
    }
    fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateInfo.postValue(PlayerStateInfo(STATE_PLAYING, formatTime))
        mainThreadHandler.post(updateTime())
    }

    override fun onCleared() {
        super.onCleared()
        resetInfo()
    }
}