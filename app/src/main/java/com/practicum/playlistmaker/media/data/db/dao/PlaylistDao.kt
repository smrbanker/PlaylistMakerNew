package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistEntity(playlist : PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylistEntity(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists ORDER BY playlistId DESC")
    suspend fun getPlaylists() : List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity?

    @Query("UPDATE playlists SET playlistList = :playlistList, playlistCount = :playlistCount WHERE playlistId = :playlistId")
    suspend fun deleteTrackByID(playlistList: String, playlistCount: Int, playlistId: Int)

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)
}