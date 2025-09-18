package com.practicum.playlistmaker.player.data

interface MediaPlayerRepositories {
    fun prepare(trackUrl:String,onPrepared:()->Unit,onCompletion:()->Unit)
    fun startPlayer()
    fun pausePlayer()
    fun resetPlayer()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}