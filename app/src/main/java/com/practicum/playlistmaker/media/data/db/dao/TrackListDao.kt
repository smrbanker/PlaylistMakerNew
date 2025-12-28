package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.TrackListEntity

@Dao
interface TrackListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackListEntity)

    @Query("SELECT * FROM tracklist WHERE trackId IN (:trackIds) ORDER BY timeStamp DESC")
    suspend fun getTracksByIds(trackIds: List<Int>): List<TrackListEntity>

    @Query("DELETE FROM tracklist WHERE trackId = :trackId")
    suspend fun deleteTrackListEntity(trackId: Int)

    @Query("SELECT * FROM tracklist WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): TrackListEntity
}