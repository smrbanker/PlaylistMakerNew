package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.media.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackEntity(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrackEntity(track: TrackEntity)

    @Transaction
    suspend fun replaceTrackEntity(track: TrackEntity) {
        deleteTrackEntity(track)
        insertTrackEntity(track)
    }

    @Query("SELECT * FROM favourites ORDER BY timeStamp ASC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favourites")
    suspend fun getTracksId(): List<Int>
}