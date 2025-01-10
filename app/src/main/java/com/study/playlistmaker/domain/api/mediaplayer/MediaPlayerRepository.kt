package com.study.playlistmaker.domain.api.mediaplayer

interface MediaPlayerRepository {

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}
