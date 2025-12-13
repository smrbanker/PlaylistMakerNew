package com.practicum.playlistmaker.media.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.search.domain.Playlist
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaAdapterPlaylist(private val playlists: List<Playlist>, private val onPlaylistClick: (Playlist) -> Unit) : RecyclerView.Adapter<MediaViewHolderPlaylist>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolderPlaylist {
        val binding = PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolderPlaylist(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolderPlaylist, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                onPlaylistClick.invoke(playlists[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    private var isClickAllowed = true
    fun clickDebounce(): Boolean {      //задержка для двойного нажатия
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            GlobalScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}