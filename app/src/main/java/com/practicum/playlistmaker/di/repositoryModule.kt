package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.MediaPlayerRepositories
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoriesImpl
import com.practicum.playlistmaker.search.data.HistoryRepository
import com.practicum.playlistmaker.search.data.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.dto.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.network.TracksConverterImpl
import com.practicum.playlistmaker.search.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistory
import com.practicum.playlistmaker.search.domain.api.TracksConverter
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.data.SwitchThemeRepository
import com.practicum.playlistmaker.settings.data.SwitchThemeRepositoryImpl
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorRepository
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorRepositoryImpl
import com.practicum.playlistmaker.sharing.data.repository.SharingRepository
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single(qualifier = named("save")) {
        androidContext().getSharedPreferences("key_for_save", Context.MODE_PRIVATE)
    }

    single(qualifier = named("switch")) {
        androidContext().getSharedPreferences("key_for_switch", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory { MediaPlayer() }

    single<MediaPlayerRepositories> {
        MediaPlayerRepositoriesImpl(get())
    }

    single<SearchHistory<TrackDto>>{
        SearchHistoryImpl(get(qualifier = named("save")), get())
    }

    single<TracksConverter> {
        TracksConverterImpl()
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SwitchThemeRepository> {
        SwitchThemeRepositoryImpl(get(qualifier = named("switch")))
    }

    single<ExternalNavigatorRepository> {
        ExternalNavigatorRepositoryImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }
}