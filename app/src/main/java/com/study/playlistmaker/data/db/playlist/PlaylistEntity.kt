package com.study.playlistmaker.data.db.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long,
    val name: String,
    val description: String,
    val cover: String?,
    val tracks: String?,
    val tracksCount: Int,
)
