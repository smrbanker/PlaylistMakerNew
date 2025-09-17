package com.practicum.playlistmaker.search.domain.api

interface SearchHistory <T> {
    fun read (): Array<T>
    fun write (track: MutableList<T>)
    fun add (newTrack: MutableList<T>) : List<T>
    fun clear ()
}