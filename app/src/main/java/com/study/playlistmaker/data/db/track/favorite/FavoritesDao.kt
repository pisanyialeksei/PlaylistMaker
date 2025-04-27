package com.study.playlistmaker.data.db.track.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.study.playlistmaker.data.db.track.TrackEntity

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favoriteTrackEntity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun removeFromFavorites(trackId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    suspend fun isTrackFavorite(trackId: Long): Boolean

    @Query("SELECT trackId FROM favorites")
    suspend fun getAllFavoriteTrackIds(): List<Long>

    @Query("SELECT trackId FROM favorites ORDER BY timestamp DESC")
    suspend fun getFavoriteTrackIds(): List<Long>

    @Transaction
    @Query(
        """
        SELECT t.* 
        FROM tracks t
        JOIN favorites ft ON t.trackId = ft.trackId
        ORDER BY ft.timestamp DESC
    """
    )
    suspend fun getFavoriteTracks(): List<TrackEntity>
}
