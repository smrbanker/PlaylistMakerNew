package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.media.domain.impl.FavouriteInteractorImpl
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.settings.domain.SwitchThemeInteractor
import com.practicum.playlistmaker.settings.domain.SwitchThemeInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    factory<SwitchThemeInteractor> {
        SwitchThemeInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    single<FavouriteInteractor> {
        FavouriteInteractorImpl(get())
    }

}