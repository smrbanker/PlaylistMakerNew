package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistory (sp : SharedPreferences) {

    val maxTrackNumber = 10

    fun read (sp: SharedPreferences): Array<Track> {
        val json = sp.getString(SAVE_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun write (sp: SharedPreferences, track: MutableList<Track>) {
        val json = Gson().toJson(track)
        sp.edit {
            putString(SAVE_KEY, json)
        }
    }

    fun add (sp: SharedPreferences, newTrack: MutableList<Track>) : MutableList<Track> {
        val tempTrack : Array<Track> = read(sp)
        val currentTrack : MutableList<Track> = mutableListOf()
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

    fun clear (sp: SharedPreferences) {
        val currentTrack : List<Track> = emptyList()
        val json = Gson().toJson(currentTrack)
        sp.edit {
            putString(SAVE_KEY, json)
        }
    }
}