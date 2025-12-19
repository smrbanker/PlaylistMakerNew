package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.ui.create.MediaViewModelCreatePlaylist
import com.practicum.playlistmaker.media.ui.playlist.MediaViewModelPlaylist
import com.practicum.playlistmaker.media.ui.track.MediaViewModelTrack
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.HistoryViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        HistoryViewModel(get())
    }

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        MediaViewModelTrack(androidContext(),get())
    }

    viewModel { (playlist: String) ->
        MediaViewModelPlaylist(androidContext(),get())
    }

    viewModel {
        MediaViewModelCreatePlaylist(get())
    }

}