package com.study.playlistmaker.sharing.domain.impl

import com.study.playlistmaker.R
import com.study.playlistmaker.sharing.data.ExternalNavigator
import com.study.playlistmaker.sharing.data.StringProvider
import com.study.playlistmaker.sharing.domain.SharingInteractor
import com.study.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val stringProvider: StringProvider,
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.share(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return stringProvider.getString(R.string.agreement_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = stringProvider.getString(R.string.support_email),
            subject = stringProvider.getString(R.string.support_mail_subject),
            body = stringProvider.getString(R.string.support_mail_body)
        )
    }

    private fun getTermsLink(): String {
        return stringProvider.getString(R.string.agreement_link)
    }
}
