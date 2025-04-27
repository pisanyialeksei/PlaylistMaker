package com.study.playlistmaker.data.db.track.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val trackId: Long,
    val timestamp: Long = System.currentTimeMillis()
)
