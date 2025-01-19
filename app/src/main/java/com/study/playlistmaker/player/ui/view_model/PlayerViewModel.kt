package com.study.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.study.playlistmaker.R
import com.study.playlistmaker.player.domain.PlayerInteractor
import com.study.playlistmaker.player.domain.model.PlayerState
import com.study.playlistmaker.player.ui.model.PlayerScreenState
import com.study.playlistmaker.player.ui.model.PlayerTrack

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    track: PlayerTrack,
) : ViewModel() {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val currentPositionUpdateRunnable = object : Runnable {
        override fun run() {
            val currentState = _screenState.value ?: return
            if (currentState.playerState == PlayerState.PLAYING) {
                updateState(currentPosition = playerInteractor.getCurrentPosition())
                mainThreadHandler.postDelayed(this, CURRENT_POSITION_UPDATE_DELAY)
            }
        }
    }

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
            mainThreadHandler.post(currentPositionUpdateRunnable)
            updateState(
                playerState = PlayerState.PLAYING,
                playButtonBackground = pauseButtonBackground
            )
        } else {
            mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
            updateState(
                playerState = PlayerState.PAUSED,
                playButtonBackground = playButtonBackground
            )
        }
    }

    fun onPause() {
        playerInteractor.pause()
        mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
        updateState(
            playerState = PlayerState.PAUSED,
            playButtonBackground = playButtonBackground
        )
    }

    fun onDestroy() {
        playerInteractor.release()
        mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
    }

    private fun prepareTrack(track: PlayerTrack) {
        playerInteractor.prepare(
            url = track.previewUrl,
            onPrepared = {
                updateState(isPlayButtonEnabled = true)
            },
            onCompletion = {
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

    companion object {
        private const val CURRENT_POSITION_UPDATE_DELAY = 500L

        fun getViewModelFactory(
            playerInteractor: PlayerInteractor,
            track: PlayerTrack
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(playerInteractor, track)
            }
        }
    }
}
