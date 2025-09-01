package com.practicum.playlistmaker.data.dto

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.ui.tracks.SAVE_KEY

class SearchHistory (private val sp : SharedPreferences) {

    val maxTrackNumber = 10

    fun read (): Array<TrackDto> {
        val json = sp.getString(SAVE_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<TrackDto>::class.java)
    }

    fun write (track: MutableList<TrackDto>) {
        val json = Gson().toJson(track)
        sp.edit {
            putString(SAVE_KEY, json)
        }
    }

    fun add (newTrack: MutableList<TrackDto>) : List<TrackDto> {//MutableList<TrackDto> {
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

    fun clear () {
        val currentTrack : List<TrackDto> = emptyList()
        val json = Gson().toJson(currentTrack)
        sp.edit {
            putString(SAVE_KEY, json)
        }
    }
}