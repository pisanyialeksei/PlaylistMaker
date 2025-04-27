package com.study.playlistmaker.data.db.track.playlisttrack

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.study.playlistmaker.data.db.playlist.PlaylistEntity

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["playlistId"]),
        Index(value = ["trackId"])
    ]
)
data class PlaylistTrackEntity(
    val playlistId: Long,
    val trackId: Long,
    val timestamp: Long = System.currentTimeMillis()
)
