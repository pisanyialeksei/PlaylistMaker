package com.study.playlistmaker.data.db.track.playlisttrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.study.playlistmaker.data.db.track.TrackEntity

@Dao
interface PlaylistTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(playlistTrackEntity: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getPlaylistTracksCount(playlistId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_tracks WHERE trackId = :trackId)")
    suspend fun isTrackInAnyPlaylist(trackId: Long): Boolean

    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTrackIdsInPlaylist(playlistId: Long): List<Long>

    @Transaction
    @Query(
        """
        SELECT t.* 
        FROM tracks t
        JOIN playlist_tracks pt ON t.trackId = pt.trackId
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.timestamp DESC
    """
    )
    suspend fun getTracksInPlaylist(playlistId: Long): List<TrackEntity>
}
