package com.study.playlistmaker.data.db.playlist

import com.study.playlistmaker.library.domain.model.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            cover = playlist.cover,
        )
    }

    fun map(playlist: PlaylistEntity, tracksCount: Int = 0): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            cover = playlist.cover,
            tracksCount = tracksCount,
        )
    }
}
