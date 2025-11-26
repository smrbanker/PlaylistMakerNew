package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.TracksConverter
import com.practicum.playlistmaker.search.domain.Track

class TracksConverterImpl() : TracksConverter {

    override fun trackConvertToDto(track: Track): TrackDto {
        return TrackDto(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
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
            track.previewUrl,
            isFavorite = track.isFavorite)
    }

    override fun listConvertToDto(tracks: List<Track>): List<TrackDto> {
        val returnList : MutableList<TrackDto> = mutableListOf()
        var returnTrack : TrackDto
        for (track in tracks) {
            returnTrack = TrackDto(
                track.trackId,
                track.trackName,
                track.artistName,
                track.trackTime,
                track.artworkUrl100,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl,
                track.isFavorite
            )
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
                track.previewUrl,
                isFavorite = track.isFavorite)
            returnList.add(returnTrack) }

        return returnList
    }
}