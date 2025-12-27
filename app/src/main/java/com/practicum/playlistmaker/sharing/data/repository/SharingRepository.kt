package com.practicum.playlistmaker.sharing.data.repository

import com.practicum.playlistmaker.sharing.data.EmailData

interface SharingRepository {
    fun getShareAppLink():String
    fun getSupportEmailData(): EmailData
    fun getTermsLink():String
    fun getSharePlaylist(str: String):String
}