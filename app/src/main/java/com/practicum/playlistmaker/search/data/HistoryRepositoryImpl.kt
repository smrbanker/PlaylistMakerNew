package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchHistory
import com.practicum.playlistmaker.search.domain.api.TracksConverter

class HistoryRepositoryImpl(private val tracksConverter : TracksConverter,
                            private val searchHistory : SearchHistory <TrackDto>) : HistoryRepository {

    override fun trackWrite(historyListID: MutableList<Track>) {
        searchHistory.write(searchHistory.add(tracksConverter.listConvertToDto(historyListID).toMutableList()).toMutableList())
    }

    override fun trackClear() {
        searchHistory.clear()
    }

    override fun trackRead() : Array<Track> {
        return tracksConverter.listConvertFromDto(searchHistory.read().toList()).toTypedArray()
    }
}