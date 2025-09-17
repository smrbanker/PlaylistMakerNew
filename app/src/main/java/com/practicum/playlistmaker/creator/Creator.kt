package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoriesImpl
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.player.data.MediaPlayerRepositories
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SwitchThemeInteractor
import com.practicum.playlistmaker.settings.domain.SwitchThemeInteractorImpl
import com.practicum.playlistmaker.settings.data.SwitchThemeRepository
import com.practicum.playlistmaker.settings.data.SwitchThemeRepositoryImpl
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorRepositoryImpl
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorRepository
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.data.repository.SharingRepository

object Creator {

    private lateinit var application: Application

    fun initCreatorApplication(application: Application) {
        this.application = application
    }
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSharingInteractor() : SharingInteractor {
        return SharingInteractorImpl(provideSharingRepository(), provideExternalNavigator())
    }
    fun provideSharingRepository(): SharingRepository {
        return SharingRepositoryImpl(application)
    }
    fun provideExternalNavigator(): ExternalNavigatorRepository {
        return ExternalNavigatorRepositoryImpl(application)
    }

    fun provideSwitchThemeInteractor() : SwitchThemeInteractor {
        return SwitchThemeInteractorImpl(provideSwitchThemeRepository())
    }

    fun provideSwitchThemeRepository() : SwitchThemeRepository {
        return SwitchThemeRepositoryImpl()
    }

    fun provideSharedPreferences(key:String) : SharedPreferences {
        val keyPref = key
        return application.getSharedPreferences(keyPref, Context.MODE_PRIVATE)
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(MediaPlayerRepositories())
    }
    fun MediaPlayerRepositories(): MediaPlayerRepositories {
        return MediaPlayerRepositoriesImpl()
    }
}