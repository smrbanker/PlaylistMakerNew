package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.iTinesResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(text: String, text2: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(text, text2))
        if (response.resultCode == 200) {
            val trackList =  (response as iTinesResponse).results.map {
                Track(it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTime,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl) }
            return Resource.Success(trackList)
        } else {
            return Resource.Error(response.resultCode.toString())
        }
    }
}