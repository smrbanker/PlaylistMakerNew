package com.practicum.playlistmaker.search.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.main.ui.App
import com.practicum.playlistmaker.search.domain.Track

class HistoryViewModel(private val context: Context): ViewModel() {

    val historyInteractor = Creator.provideHistoryInteractor()

    fun trackWrite(historyListID: MutableList<Track>) {
        return historyInteractor.trackWrite(historyListID)
    }

    fun trackClear() {
        return historyInteractor.trackClear()
    }

    fun trackRead() : Array<Track> {
        return historyInteractor.trackRead()
    }

    companion object {
        fun getFactory(value: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                HistoryViewModel(app)
            }
        }
    }
}