package com.study.playlistmaker.search.domain.model

import com.study.playlistmaker.player.ui.model.PlayerTrack

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String?,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false,
) {

    fun toUiTrack(): PlayerTrack {
        return PlayerTrack(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            isFavorite = isFavorite,
        )
    }
}
