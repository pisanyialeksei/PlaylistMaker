package com.study.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackDao {

    @Insert
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Delete
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorites")
    suspend fun getAllTrackIds(): List<Long>
}
