package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Track

interface HistoryRepository {
    fun trackWrite(historyListID: MutableList<Track>)
    fun trackClear()
    fun trackRead(): Array<Track>
}