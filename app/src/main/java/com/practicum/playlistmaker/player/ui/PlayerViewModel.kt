package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.media.ui.playlist.MediaStatePlaylist
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel (
    private val playerInteractor : MediaPlayerInteractor,
    private val favouriteInteractor: FavouriteInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    private var formatTime = "00:00"
    private var timerJob: Job? = null
    private var favourJob: Job? = null

    val playerStateInfo = MutableLiveData(PlayerStateInfo(STATE_DEFAULT, "00:00"))
    fun observePlayerStateInfo(): LiveData<PlayerStateInfo> = playerStateInfo

    val favouriteInfo = MutableLiveData<Boolean>()
    fun observeFavouriteInfo(): LiveData<Boolean> = favouriteInfo

    val checkTrackInfo = MutableLiveData(PlayerCheckInfo(false, ""))
    fun observeCheckInfo(): LiveData<PlayerCheckInfo> = checkTrackInfo

    private val stateLiveData = MutableLiveData<MediaStatePlaylist>()
    fun observePlaylistState(): LiveData<MediaStatePlaylist> = stateLiveData

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
        timerJob?.cancel()
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

    fun changeFavourite(track: Track) {
        viewModelScope.launch {
            val favourite = favouriteInfo.value ?: false
            if (favourite)
                favouriteInteractor.deleteTrack(track)
            else
                favouriteInteractor.addTrack(track)
            renderFavorite(!favourite)
        }
    }

    fun checkFavourite(trackId: Int) {
        favourJob = viewModelScope.launch {
            favouriteInteractor.favouriteTracksID().collect {value ->
                if (trackId in value) renderFavorite(true)
                else renderFavorite(false)
            }
        }
    }

    private fun renderFavorite(favourite: Boolean) {
        favouriteInfo.postValue(favourite)
    }

    override fun onCleared() {
        super.onCleared()
        resetInfo()
    }

    fun onDestroy() {
        resetInfo()
        favourJob?.cancel()
    }

    fun returnPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect{
                        playlists -> processResults(playlists)
                }
        }
    }

    fun checkTrack(playlist: Playlist, id : String) {
        val trackArray = playlist.playlistList?.split(",")
        if (trackArray?.contains(id) == true ) renderCheck(true, playlist.playlistName)
        //else renderCheck(false)
    }

    private fun renderCheck(check: Boolean, name : String) {
        checkTrackInfo.postValue(PlayerCheckInfo(check,name))
    }

    private fun processResults(playlists : List<Playlist>) {
        if(playlists.isEmpty()) {
            renderState(MediaStatePlaylist.Empty(""))
        } else {
            renderState(MediaStatePlaylist.Content(playlists))
        }
    }

    private fun renderState(state: MediaStatePlaylist) {
        stateLiveData.postValue(state)
    }

    //Добавляем трек в плейлист
    //fun updatePlaylist(playlist : Playlist) {
    //    viewModelScope.launch(Dispatchers.IO) {
    //        playlistsInteractor.updatePlaylist(playlist)
    //    }
    //}
    //fun insertTrackInPlaylist(track: Track) {
    //    viewModelScope.launch(Dispatchers.IO) {
    //        playlistsInteractor.insertTrackInPlaylist(track)
    //    }
    //}

    private var isClickAllowed = true
    fun clickDebounce(): Boolean {      //задержка для двойного нажатия
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETE = 4
        const val DELAY = 300L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}