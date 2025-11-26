package com.practicum.playlistmaker.search.data.dto

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.media.data.AppDatabase
import com.practicum.playlistmaker.search.domain.api.SearchHistory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchHistoryImpl (
    private val sp : SharedPreferences,
    private val gson : Gson,
    private val appDatabase: AppDatabase
) : SearchHistory <TrackDto> {

    val maxTrackNumber = 10

    override fun read (): Array<TrackDto> {
        val json = sp.getString(SAVE_KEY, null) ?: return emptyArray()
        val arrayTrackDto = gson.fromJson(json, Array<TrackDto>::class.java)
        GlobalScope.launch {
            for (elem in arrayTrackDto) {
                elem.isFavorite = isLiked(elem.trackId)
            }
        }
        return arrayTrackDto
    }

    override fun write (track: MutableList<TrackDto>) {
        val json = gson.toJson(track)
        sp.edit {
            putString(SAVE_KEY, json)
        }
    }

    override fun add (newTrack: MutableList<TrackDto>) : List<TrackDto> {//MutableList<TrackDto> {
        val tempTrack : Array<TrackDto> = read()
        val currentTrack : MutableList<TrackDto> = mutableListOf()
        currentTrack.addAll(tempTrack)
        var newTrackID : Int
        var newTrackBool = true

        if (newTrack.isNotEmpty()) {

            for (i in newTrack.indices) { //список новых треков не пустой, проходим весь список
                newTrackID = newTrack[i].trackId

                for (j in currentTrack.indices) { // проверка если такой трек уже есть
                    if (newTrackID == currentTrack[j].trackId) {
                        currentTrack.removeAt(j)
                        currentTrack.add(0, newTrack[i])
                        newTrackBool = false
                        break
                    }
                }

                if (newTrackBool) { // такого трека нет в текущем списке
                    if (currentTrack.size >= maxTrackNumber) {
                        currentTrack.removeAt(maxTrackNumber - 1)
                        currentTrack.add(0, newTrack[i])
                    } else {
                        currentTrack.add(0, newTrack[i])
                    }
                }
            }
        }
        return currentTrack
    }

    override fun clear () {
        val currentTrack : List<TrackDto> = emptyList()
        val json = gson.toJson(currentTrack)
        sp.edit {
            putString(SAVE_KEY, json)
        }
    }

    private suspend fun isLiked(id : Int) : Boolean {
        return id in appDatabase.trackDao().getTracksId()
    }

    companion object {
        private const val SAVE_KEY = "key_for_save"
    }
}