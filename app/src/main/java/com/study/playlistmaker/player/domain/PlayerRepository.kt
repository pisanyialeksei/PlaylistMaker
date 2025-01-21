package com.study.playlistmaker.player.domain

interface PlayerRepository {

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}
