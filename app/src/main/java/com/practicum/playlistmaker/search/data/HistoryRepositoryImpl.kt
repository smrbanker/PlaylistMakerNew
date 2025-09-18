package com.practicum.playlistmaker.search.data

import android.content.Context
import com.practicum.playlistmaker.search.data.dto.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.network.TracksConverterImpl
import com.practicum.playlistmaker.search.domain.Track

class HistoryRepositoryImpl(private val context: Context) : HistoryRepository {

    val sp = context.getSharedPreferences(SAVE_LIST, Context.MODE_PRIVATE)
    val searchHistory = SearchHistoryImpl(sp)
    val tracksConverter = TracksConverterImpl()

    override fun trackWrite(historyListID: MutableList<Track>) {
        searchHistory.write(searchHistory.add(tracksConverter.listConvertToDto(historyListID).toMutableList()).toMutableList())
    }

    override fun trackClear() {
        searchHistory.clear()
    }

    override fun trackRead() : Array<Track> {
        return tracksConverter.listConvertFromDto(searchHistory.read().toList()).toTypedArray()
    }

    companion object {
        private const val SAVE_LIST = "save_list"

    }
}