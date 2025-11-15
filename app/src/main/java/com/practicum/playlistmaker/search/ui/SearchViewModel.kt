package com.practicum.playlistmaker.search.ui

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor : TracksInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast
    private var textInput = ""

    private val trackSearchDebounce = debounce<TextView>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) {
        inputEditText -> iAPICall(inputEditText)
    }
    fun searchDebounce(inputEditText : TextView) {
        if (textInput != inputEditText.toString()) {
            this.textInput = inputEditText.toString()
            trackSearchDebounce(inputEditText)
        }
    }

    fun iAPICall (inputEditText : TextView) {
        if (inputEditText.text.isNotEmpty()) {
            renderState(SearchState.Loading)
            searchTracks(inputEditText)
        }
    }

    private fun searchTracks(inputEditText : TextView) {
        viewModelScope.launch {
            tracksInteractor
                .searchTracks(inputEditText.text.toString(), "song")
                .collect { pair -> processResult(pair.first, pair.second) }
            }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(SearchState.Error(errorMessage))
            }
            tracks.isEmpty() -> {
                renderState(SearchState.Empty)
            }
            else -> {
                renderState(SearchState.Content(tracks))
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}