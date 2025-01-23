package com.study.playlistmaker.sharing.data.impl

import android.content.Context
import com.study.playlistmaker.sharing.data.StringProvider

class StringProviderImpl(private val context: Context) : StringProvider {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}
