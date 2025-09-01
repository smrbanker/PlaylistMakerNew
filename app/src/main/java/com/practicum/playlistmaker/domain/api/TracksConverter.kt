package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.Track

interface TracksConverter {
    fun trackConvertToDto(track: Track): TrackDto
    fun trackConvertFromDto(track: TrackDto): Track
    fun listConvertToDto(tracks: List<Track>): List<TrackDto>
    fun listConvertFromDto(tracks: List<TrackDto>): List<Track>
}