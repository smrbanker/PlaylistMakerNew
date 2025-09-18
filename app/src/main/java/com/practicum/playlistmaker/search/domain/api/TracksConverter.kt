package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.Track

interface TracksConverter {
    fun trackConvertToDto(track: Track): TrackDto
    fun trackConvertFromDto(track: TrackDto): Track
    fun listConvertToDto(tracks: List<Track>): List<TrackDto>
    fun listConvertFromDto(tracks: List<TrackDto>): List<Track>
}