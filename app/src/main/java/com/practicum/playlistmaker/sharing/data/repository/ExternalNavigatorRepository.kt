package com.practicum.playlistmaker.sharing.data.repository

import com.practicum.playlistmaker.sharing.data.EmailData

interface ExternalNavigatorRepository {
    fun shareLink(text:String)
    fun openLink(link:String)
    fun openEmail(email: EmailData)
}