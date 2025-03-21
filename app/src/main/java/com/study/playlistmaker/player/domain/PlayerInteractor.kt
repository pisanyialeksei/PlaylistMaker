package com.study.playlistmaker.player.domain

interface PlayerInteractor {

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun setPlaybackControl()
    fun isPlaying(): Boolean
}
