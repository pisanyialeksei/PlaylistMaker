package com.study.playlistmaker.sharing.data

import com.study.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {

    fun share(text: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}
