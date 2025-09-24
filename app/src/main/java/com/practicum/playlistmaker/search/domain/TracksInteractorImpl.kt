package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.api.Resource
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, text2: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            val response = repository.searchTracks(text, text2)
            when (response) {
                is Resource.Success -> {
                    consumer.consume(response.data, null)
                }
                is Resource.Error -> {
                    consumer.consume(null, response.message)
                }
            }
        }
    }
}