package com.practicum.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.entity.TrackListEntity
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
            image = playlist.playlistImage.let { saveImageToPrivateStorage(playlist.playlistImage.toUri()) }
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

    override suspend fun getDuration(playlistID: Int): Int {
        var durationSum = 0
        val trackIdArrayInt = getTrackListInInt(playlistID)
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistID)
        if (playlist?.playlistCount == 0) return 0

        val trackArray = appDatabase.tracklistDao().getTracksByIds(trackIdArrayInt)

        for (i in 0..(trackArray.size-1))
            durationSum += trackArray[i].trackTime

        return SimpleDateFormat("mm", Locale.getDefault()).format(durationSum).toInt()
    }

    override fun getTracks(playlistID: Int): Flow<List<Track>> = flow {
        val trackIdArrayInt = getTrackListInInt(playlistID)
        val tracks: MutableList<TrackListEntity> = mutableListOf()

        for (i in (trackIdArrayInt.size-1) downTo 0) {
            tracks.add(appDatabase.tracklistDao().getTrackById(trackIdArrayInt[i]))
            emit(convertFromTrackListEntity(tracks))
        }
    }

    private fun convertFromTrackListEntity(tracks: List<TrackListEntity>): List<Track> {
        return tracks.map { track -> trackListDbConvertor.map(track) }
    }

    override suspend fun deleteTrack (trackID: Int, playlistID: Int) {
        val trackIdArrayInt = getTrackListInInt(playlistID)
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistID)

        trackIdArrayInt.remove(trackID)
        var string = ""

        if (trackIdArrayInt.isEmpty()) {
            appDatabase.playlistDao().deleteTrackByID(
                string,
                0,
                playlist?.playlistId ?: 0)
        }
        else {
            for (i in 0..(trackIdArrayInt.size.minus(1))) {
                string = string + trackIdArrayInt[i].toString() + ","
            }
            appDatabase.playlistDao().deleteTrackByID(
                string,
                playlist?.playlistCount?.minus(1) ?: 0,
                playlist?.playlistId ?: 0)
        }

        val playlists = appDatabase.playlistDao().getPlaylists()
        var flagPlaylist = true
        for (i in 0..(playlists.size.minus(1))) {
            if (checkTrackInPlaylist(trackID, playlists[i].playlistId)) flagPlaylist = false
        }
        if (flagPlaylist) appDatabase.tracklistDao().deleteTrackListEntity(trackID)
    }

    suspend fun checkTrackInPlaylist(trackID: Int, playlistID : Int): Boolean {
        val trackIdArrayInt = getTrackListInInt(playlistID)
        return trackID in trackIdArrayInt
    }

    override suspend fun getPlaylistCount(playlistID: Int): Int {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistID)
        return playlist?.playlistCount ?: 0
    }

    override suspend fun getPlaylistInfo(playlistID: Int): String {

        val trackIdArrayInt = getTrackListInInt(playlistID)
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistID)
        val tracks = appDatabase.tracklistDao().getTracksByIds(trackIdArrayInt)

        var string = ""
        var duration : String

        val number = context.resources.getQuantityString(
            R.plurals.numberOfTracks,
            playlist?.playlistCount ?: 0,
            playlist?.playlistCount ?: 0
        )

        string = string + playlist?.playlistName + "\n" + playlist?.playlistDescription + "\n" + number

        for (i in 0..(tracks.size-1)) {
            duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(tracks[i].trackTime)
            string = string + "\n" + "${i+1}" + ". " + tracks[i].artistName + " - " + tracks[i].trackName + " (" + duration + ")"
        }
        return string
    }

    override suspend fun deletePlaylist (playlistID: Int) {

        val trackIdArrayInt = getTrackListInInt(playlistID)

        appDatabase.playlistDao().deletePlaylist(playlistID) //УДАЛЯЕМ ПЛЕЙЛИСТ

        val playlists = appDatabase.playlistDao().getPlaylists()
        var flagPlaylist : Boolean

        if (trackIdArrayInt.isNotEmpty()) { //ЦИКЛ ПО ТРЕКАМ И УДАЛЯЕМ, ЕСЛИ БОЛЬШЕ НЕТ
            for (i in 0..(trackIdArrayInt.size.minus(1))) {
                flagPlaylist = true
                for (j in 0..(playlists.size.minus(1))) {
                    if (checkTrackInPlaylist(trackIdArrayInt[i], playlists[j].playlistId)) flagPlaylist = false
                }
                if (flagPlaylist) appDatabase.tracklistDao().deleteTrackListEntity(trackIdArrayInt[i])
            }
        }
    }

    suspend fun getTrackListInInt(playlistID: Int) : MutableList<Int> {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistID)
        val trackIdArrayString = playlist?.playlistList?.split(",")
        val trackIdArrayInt: MutableList<Int> = mutableListOf()

        for (i in 0..(trackIdArrayString?.size?.minus(2) ?: 0)) {
            trackIdArrayInt.add(trackIdArrayString?.get(i)?.toInt() ?: 0)
        }
        return trackIdArrayInt
    }

    override suspend fun updatePlaylist(playlist: Playlist, playlistName : String,
                                        playlistDescription : String?, playlistUri : String?) {
        appDatabase.playlistDao().updatePlaylistEntity(PlaylistEntity(
            playlist.playlistId ?: 0,
            playlistName,
            playlistDescription ?: "",
            playlistUri ?: "",
            playlist.playlistList,
            playlist.playlistCount)
        )
    }

    override suspend fun updatePlaylistView(playlistID: Int): Playlist {
        return playlistDbConvertor.map(appDatabase.playlistDao().getPlaylistById(playlistID)!!)
    }
}