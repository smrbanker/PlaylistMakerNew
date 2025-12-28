package com.practicum.playlistmaker.sharing.domain

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(str : String)
}