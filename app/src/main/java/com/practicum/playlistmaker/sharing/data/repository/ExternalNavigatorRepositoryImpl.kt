package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.sharing.data.EmailData

class ExternalNavigatorRepositoryImpl(val context: Context): ExternalNavigatorRepository {

    override fun openLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun openEmail(email: EmailData) {
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = "mailto:".toUri()
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, email.themeEmail)
        shareIntent.putExtra(Intent.EXTRA_TEXT, email.messageEmail)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun shareLink(text: String) {
        val address = text.toUri()
        val shareIntent = Intent(Intent.ACTION_VIEW, address)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun sharePlaylist(text: String) {
        val intentSend = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Intent.createChooser(this, null)
        }
        context.startActivity(intentSend, null)
    }
}