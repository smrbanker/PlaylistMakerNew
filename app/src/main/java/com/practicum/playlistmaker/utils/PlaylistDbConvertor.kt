package com.practicum.playlistmaker.utils

import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.domain.Playlist

class PlaylistDbConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId!!,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistImage = playlist.playlistImage,
            playlistList = playlist.playlistList,
            playlistCount = playlist.playlistCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistImage = playlist.playlistImage,
            playlistList = playlist.playlistList,
            playlistCount = playlist.playlistCount
        )
    }
}