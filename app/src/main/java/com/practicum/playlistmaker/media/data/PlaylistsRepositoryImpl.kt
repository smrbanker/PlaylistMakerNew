package com.practicum.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.PlaylistDbConvertor
import com.practicum.playlistmaker.utils.TrackListDbConvertor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackListDbConvertor: TrackListDbConvertor,
    private val context: Context
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        var image : String?
        var playlistWithImage : Playlist
        if (playlist.playlistImage != null) {
            image = playlist.playlistImage?.let { saveImageToPrivateStorage(playlist.playlistImage.toUri()) }
            playlistWithImage = playlist.copy(playlistImage = image)
        } else { playlistWithImage = playlist.copy(playlistImage = "") }
        appDatabase.playlistDao().insertPlaylistEntity(playlistDbConvertor.map(playlistWithImage))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "playlist_cover_$timestamp.jpg"
        val file = File(filePath, fileName)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }
        return file.absolutePath
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        appDatabase.tracklistDao().insertTrack(trackListDbConvertor.map(track))
        val playlistEntity = playlistDbConvertor.map(playlist)
        val getPlaylistEntity = appDatabase.playlistDao().getPlaylistById(playlistEntity.playlistId)
        val newTrackList = getPlaylistEntity?.playlistList + track.trackId + ","
        appDatabase.playlistDao().updatePlaylistEntity(PlaylistEntity(
            getPlaylistEntity?.playlistId ?: 0,
            getPlaylistEntity?.playlistName ?: "",
            getPlaylistEntity?.playlistDescription,
            getPlaylistEntity?.playlistImage,
            newTrackList,
            getPlaylistEntity?.playlistCount?.plus(1) ?: 0)
        )
    }
}