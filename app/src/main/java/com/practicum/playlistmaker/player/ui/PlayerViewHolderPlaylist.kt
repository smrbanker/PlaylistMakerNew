package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlayerItemBinding
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.search.domain.Playlist

class PlayerViewHolderPlaylist(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val playlistTitle: TextView = itemView.findViewById(R.id.playlistTitle)
    private val playlistInfo: TextView = itemView.findViewById(R.id.playlistInfo)
    private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImage)

    fun bind(model: Playlist) {

        playlistTitle.text = model.playlistName
        playlistInfo.text = countTracks(model.playlistCount)
        val context = itemView.context
        val trackImage = model.playlistImage
        val trackUrl = playlistImage
        val placeholder = R.drawable.placeholder
        setImage(context, trackImage, trackUrl, placeholder, 8)
    }

    private fun countTracks(countTrack: Int) : String {
        return  if (countTrack % 10 == 1) { "$countTrack трек" }
        else {  if ((countTrack % 10 == 2) or (countTrack % 10 == 3) or (countTrack % 10 == 4)) { "$countTrack трека" }
        else    if ((countTrack == 11) or (countTrack == 12) or (countTrack == 13) or (countTrack == 14)) { "$countTrack треков" }
        else { "$countTrack треков" }
        }
    }

    fun setImage(itemView: Context, trackImage: String?, trackUrl: ImageView, placeholder: Int, dp: Int) {
        Glide.with(itemView).load(trackImage)
            .transform(CenterCrop(), RoundedCorners(dp))
            .placeholder(placeholder).into(trackUrl)
    }
}