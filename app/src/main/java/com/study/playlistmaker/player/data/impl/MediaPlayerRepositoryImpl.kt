package com.study.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.study.playlistmaker.player.data.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository {

    private val mediaPlayer = MediaPlayer()

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}
