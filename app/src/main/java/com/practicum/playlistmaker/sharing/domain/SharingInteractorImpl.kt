package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.sharing.data.EmailData
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorRepository
import com.practicum.playlistmaker.sharing.data.repository.SharingRepository

class SharingInteractorImpl(
    val repository: SharingRepository,
    private val externalNavigator: ExternalNavigatorRepository
):SharingInteractor
{
    override fun shareApp() { externalNavigator.shareLink(getShareAppLink()) }

    override fun openTerms() { externalNavigator.openLink(getTermsLink()) }

    override fun openSupport() { externalNavigator.openEmail(getSupportEmailData()) }

    override fun sharePlaylist(str: String) { externalNavigator.sharePlaylist(getSharePlaylist(str)) }

    private fun getShareAppLink(): String { return repository.getShareAppLink() }

    private fun getSupportEmailData(): EmailData { return repository.getSupportEmailData() }

    private fun getTermsLink(): String { return repository.getTermsLink() }

    private fun getSharePlaylist(str : String): String { return repository.getSharePlaylist(str) }
}