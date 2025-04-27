package com.study.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.study.playlistmaker.data.db.playlist.PlaylistEntity
import com.study.playlistmaker.data.db.playlist.PlaylistsDao
import com.study.playlistmaker.data.db.track.TrackEntity
import com.study.playlistmaker.data.db.track.TracksDao
import com.study.playlistmaker.data.db.track.favorite.FavoriteEntity
import com.study.playlistmaker.data.db.track.favorite.FavoritesDao
import com.study.playlistmaker.data.db.track.playlisttrack.PlaylistTrackEntity
import com.study.playlistmaker.data.db.track.playlisttrack.PlaylistTracksDao

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, FavoriteEntity::class, PlaylistTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TracksDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}
