package com.study.playlistmaker.player.ui.view_model

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.R
import com.study.playlistmaker.player.domain.PlayerInteractor
import com.study.playlistmaker.player.domain.model.PlayerState
import com.study.playlistmaker.player.ui.model.PlayerScreenState
import com.study.playlistmaker.player.ui.model.PlayerTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    track: PlayerTrack,
) : ViewModel() {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var timerJob: Job? = null

    @DrawableRes
    private val playButtonBackground: Int = R.drawable.play_button_background

    @DrawableRes
    private val pauseButtonBackground: Int = R.drawable.pause_button_background

    init {
        _screenState.value = PlayerScreenState(
            track = track,
            playerState = PlayerState.DEFAULT,
            currentPosition = 0,
            isPlayButtonEnabled = false,
            playButtonBackground = playButtonBackground
        )
        prepareTrack(track)
    }

    fun togglePlayback() {
        playerInteractor.setPlaybackControl()
        if (playerInteractor.isPlaying()) {
            startTimer()
            updateState(
                playerState = PlayerState.PLAYING,
                playButtonBackground = pauseButtonBackground
            )
        } else {
            timerJob?.cancel()
            updateState(
                playerState = PlayerState.PAUSED,
                playButtonBackground = playButtonBackground
            )
        }
    }

    fun onPause() {
        playerInteractor.pause()
        timerJob?.cancel()
        updateState(
            playerState = PlayerState.PAUSED,
            playButtonBackground = playButtonBackground
        )
    }

    fun onDestroy() {
        playerInteractor.release()
        timerJob?.cancel()
    }

    private fun prepareTrack(track: PlayerTrack) {
        playerInteractor.prepare(
            url = track.previewUrl,
            onPrepared = {
                updateState(isPlayButtonEnabled = true)
            },
            onCompletion = {
                timerJob?.cancel()
                updateState(
                    playerState = PlayerState.PREPARED,
                    currentPosition = 0,
                    playButtonBackground = playButtonBackground
                )
            }
        )
    }

    private fun updateState(
        playerState: PlayerState? = null,
        currentPosition: Int? = null,
        isPlayButtonEnabled: Boolean? = null,
        playButtonBackground: Int? = null
    ) {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(
            playerState = playerState ?: currentState.playerState,
            currentPosition = currentPosition ?: currentState.currentPosition,
            isPlayButtonEnabled = isPlayButtonEnabled ?: currentState.isPlayButtonEnabled,
            playButtonBackground = playButtonBackground ?: currentState.playButtonBackground
        )
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(CURRENT_POSITION_UPDATE_DELAY)
                updateState(currentPosition = playerInteractor.getCurrentPosition())
            }
        }
    }

    companion object {
        private const val CURRENT_POSITION_UPDATE_DELAY = 300L
    }
}
