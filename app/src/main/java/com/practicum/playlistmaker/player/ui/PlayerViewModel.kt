package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel (private val playerInteractor : MediaPlayerInteractor) : ViewModel() {
    private var formatTime = "00:00"
    private var timerJob: Job? = null

    val playerStateInfo = MutableLiveData(PlayerStateInfo(STATE_DEFAULT, "00:00"))
    fun observePlayerStateInfo(): LiveData<PlayerStateInfo> = playerStateInfo

    fun resetInfo() {
        timerJob?.cancel()
        playerInteractor.resetPlayer()
    }

    private fun stopCountTimer() {
        timerJob?.cancel()
    }

    fun preparePlayer(url : String) {
        if (playerStateInfo.value?.state == STATE_DEFAULT) {
            playerInteractor.prepare(
                url,
                onPrepared = {
                    playerStateInfo.postValue(
                        PlayerStateInfo(
                            STATE_PREPARED,
                            formatTime
                        )
                    )
                },
                onCompletion = {
                    playerStateInfo.postValue(PlayerStateInfo(STATE_COMPLETE, "00:00"))
                    stopCountTimer()
                }
            )
        }
    }

    private fun updateTimeC() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                formatTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentPosition())
                playerStateInfo.postValue(PlayerStateInfo(STATE_PLAYING, formatTime))
                delay(DELAY)
            }
        }
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerStateInfo.postValue(PlayerStateInfo(STATE_PAUSED, formatTime))
        stopCountTimer()
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateInfo.postValue(PlayerStateInfo(STATE_PLAYING, formatTime))
        updateTimeC()
    }

    override fun onCleared() {
        super.onCleared()
        resetInfo()
    }

    fun onDestroy() {
        resetInfo()
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETE = 4
        const val DELAY = 200L
    }
}