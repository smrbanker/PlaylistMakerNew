package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val songLogo: ImageView = itemView.findViewById(R.id.search_track_image)
    private val songName: TextView = itemView.findViewById(R.id.search_track_song)
    private val songSinger: TextView = itemView.findViewById(R.id.search_track_singer)
    private val songLong: TextView = itemView.findViewById(R.id.search_track_length)

    fun bind(model: Track) {

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(R.dimen.rCorners))
            .into(songLogo)

        songName.text = model.trackName
        songSinger.text = model.artistName
        songLong.text = model.trackTime
    }
}