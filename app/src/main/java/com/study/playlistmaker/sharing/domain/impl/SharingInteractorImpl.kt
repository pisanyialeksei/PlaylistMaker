package com.study.playlistmaker.sharing.domain.impl

import android.content.Context
import com.study.playlistmaker.R
import com.study.playlistmaker.sharing.data.ExternalNavigator
import com.study.playlistmaker.sharing.domain.SharingInteractor
import com.study.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.agreement_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_mail_subject),
            body = context.getString(R.string.support_mail_body)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.agreement_link)
    }
}
