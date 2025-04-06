package com.study.playlistmaker.data.db.playlist

import com.study.playlistmaker.library.domain.model.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = 0L,
            name = playlist.name,
            description = playlist.description,
            cover = playlist.cover,
            tracks = playlist.tracks,
            tracksCount = playlist.tracksCount,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            name = playlist.name,
            description = playlist.description,
            cover = playlist.cover,
            tracks = playlist.tracks,
            tracksCount = playlist.tracksCount,
        )
    }
}
