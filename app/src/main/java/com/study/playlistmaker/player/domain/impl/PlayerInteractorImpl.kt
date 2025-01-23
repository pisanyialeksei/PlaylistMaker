package com.study.playlistmaker.player.domain.impl

import com.study.playlistmaker.player.domain.PlayerInteractor
import com.study.playlistmaker.player.domain.PlayerRepository
import com.study.playlistmaker.player.domain.model.PlayerState

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    private var state = PlayerState.DEFAULT

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(
            url = url,
            onPrepared = {
                state = PlayerState.PREPARED
                onPrepared()
            },
            onCompletion = {
                state = PlayerState.PREPARED
                onCompletion()
            }
        )
    }

    override fun start() {
        repository.start()
        state = PlayerState.PLAYING
    }

    override fun pause() {
        repository.pause()
        state = PlayerState.PAUSED
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun setPlaybackControl() {
        when (state) {
            PlayerState.PLAYING -> pause()
            PlayerState.PREPARED, PlayerState.PAUSED -> start()
            else -> {}
        }
    }

    override fun isPlaying(): Boolean {
        return state == PlayerState.PLAYING
    }
}
