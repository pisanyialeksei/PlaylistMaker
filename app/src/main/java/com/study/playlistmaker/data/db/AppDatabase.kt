package com.study.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.study.playlistmaker.data.db.playlist.PlaylistEntity
import com.study.playlistmaker.data.db.playlist.PlaylistsDao
import com.study.playlistmaker.data.db.track.TrackEntity
import com.study.playlistmaker.data.db.track.TracksDao

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TracksDao
    abstract fun playlistDao(): PlaylistsDao
}
