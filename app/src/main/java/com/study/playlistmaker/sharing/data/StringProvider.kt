package com.study.playlistmaker.sharing.data

interface StringProvider {

    fun getString(resId: Int): String
    fun getString(resId: Int, vararg args: String): String
}
