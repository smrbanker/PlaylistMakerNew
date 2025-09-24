package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(private val tracksInteractor : TracksInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private val handler = Handler(Looper.getMainLooper()) //handler для clickDebounce двойного нажатия
    private var isClickAllowed = true //boolean для clickDebounce двойного нажатия
    private var searchRunnable = Runnable {null} //запуск поиску по таймеру 2 сек
    private var textInput = ""

    fun searchDebounce(inputEditText : TextView) {
        if (textInput == inputEditText.toString()) {
            return
        }

        this.textInput = inputEditText.toString()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { iAPICall(inputEditText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun iAPICall (inputEditText : TextView) {
        if (inputEditText.text.isNotEmpty()) {
            renderState(SearchState.Loading)
            searchTracks(inputEditText)
        }
    }

    private fun searchTracks(inputEditText : TextView) {
        tracksInteractor.searchTracks(inputEditText.text.toString(), "song",object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                handler.post {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                SearchState.Error(errorMessage),
                                )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                SearchState.Empty
                                )
                        }

                        else -> {
                            renderState(
                                SearchState.Content(tracks)
                            )
                        }
                    }

                }
            }
        })
    }

    fun clickDebounce() : Boolean {     //задержка для двойного нажатия
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}