package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.EmailData

class SharingRepositoryImpl(val context : Context) : SharingRepository {

    override fun getShareAppLink(): String { return context.getString(R.string.course_link) }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.my_mail),
            context.getString(R.string.mail_subject),
            context.getString(R.string.mail_text)
        )
    }

    override fun getTermsLink(): String { return context.getString(R.string.agreement) }

    override fun getSharePlaylist(str: String): String {
        return str
    }
}