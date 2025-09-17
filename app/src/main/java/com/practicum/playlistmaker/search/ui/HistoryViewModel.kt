package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.main.ui.App
import com.practicum.playlistmaker.search.data.dto.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.network.TracksConverterImpl
import com.practicum.playlistmaker.search.domain.Track


class HistoryViewModel(private val context: Context): ViewModel() {

    val sp = context.getSharedPreferences(SAVE_LIST, MODE_PRIVATE)
    val searchHistory = SearchHistoryImpl(sp)
    val tracksConverter = TracksConverterImpl()

    fun trackWrite(historyListID: MutableList<Track>) {
        searchHistory.write(searchHistory.add(tracksConverter.listConvertToDto(historyListID).toMutableList()).toMutableList())
    }

    fun trackClear() {
        searchHistory.clear()
    }

    fun trackRead() : Array<Track> {
        return tracksConverter.listConvertFromDto(searchHistory.read().toList()).toTypedArray()
    }

    companion object {
        private const val SAVE_LIST = "save_list"
        fun getFactory(value: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                HistoryViewModel(app)
            }
        }
    }
}