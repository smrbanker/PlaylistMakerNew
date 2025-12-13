package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Playlist
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerAdapterPlaylist(private val playlists: List<Playlist>, private val onPlaylistClick: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlayerViewHolderPlaylist>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolderPlaylist {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false)
        return PlayerViewHolderPlaylist(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolderPlaylist, position: Int) {
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