package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.Track

class HistoryViewModel(private val historyInteractor : HistoryInteractor): ViewModel() {

    fun trackWrite(historyListID: MutableList<Track>) {
        return historyInteractor.trackWrite(historyListID)
    }

    fun trackClear() {
        return historyInteractor.trackClear()
    }

    fun trackRead() : Array<Track> {
        return historyInteractor.trackRead()
    }
}