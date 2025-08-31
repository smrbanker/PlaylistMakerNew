package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.api.TracksConverter
import com.practicum.playlistmaker.domain.models.Track

class TracksConverterImpl() : TracksConverter {

    override fun trackConvertToDto(track: Track): TrackDto {
        return TrackDto(track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }

    override fun trackConvertFromDto(track: TrackDto): Track {
        return Track(track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }

    override fun listConvertToDto(tracks: List<Track>): List<TrackDto> {
        val returnList : MutableList<TrackDto> = mutableListOf()
        var returnTrack : TrackDto
        for (track in tracks) {
            returnTrack = TrackDto(track.trackId,
                track.trackName,
                track.artistName,
                track.trackTime,
                track.artworkUrl100,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl)
            returnList.add(returnTrack) }

        return returnList
    }

    override fun listConvertFromDto(tracks: List<TrackDto>): List<Track> {
        val returnList : MutableList<Track> = mutableListOf()
        var returnTrack : Track
        for (track in tracks) {
            returnTrack = Track(track.trackId,
                track.trackName,
                track.artistName,
                track.trackTime,
                track.artworkUrl100,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl)
            returnList.add(returnTrack) }

        return returnList
    }
}