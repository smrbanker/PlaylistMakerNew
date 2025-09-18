package com.practicum.playlistmaker.search.domain

interface HistoryInteractor {
    fun trackWrite(historyListID: MutableList<Track>)
    fun trackClear()
    fun trackRead(): Array<Track>
}