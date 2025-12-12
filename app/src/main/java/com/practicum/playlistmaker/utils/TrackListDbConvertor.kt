package com.practicum.playlistmaker.utils

import com.practicum.playlistmaker.media.data.db.entity.TrackListEntity
import com.practicum.playlistmaker.search.domain.Track

class TrackListDbConvertor {

    fun map(track: Track): TrackListEntity {
        return TrackListEntity(track.trackId, track.trackName, track.artistName, track.trackTime, track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.previewUrl)
    }

    fun map(track: TrackListEntity): Track {
        return Track(track.trackId, track.trackName, track.artistName, track.trackTime, track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.previewUrl)
    }
}