package com.study.playlistmaker.player.ui.model

import com.study.playlistmaker.player.domain.model.PlayerState

data class PlayerScreenState(
    val track: PlayerTrack,
    val playerState: PlayerState,
    val currentPosition: Int,
    val isPlayButtonEnabled: Boolean,
    val playButtonBackground: Int,
    val isFavorite: Boolean,
)
