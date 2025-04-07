package com.study.playlistmaker.library.domain.model

data class Playlist(
    val name: String,
    val description: String,
    val cover: String? = null,
    val tracks: String? = null,
    val tracksCount: Int = 0,
)
