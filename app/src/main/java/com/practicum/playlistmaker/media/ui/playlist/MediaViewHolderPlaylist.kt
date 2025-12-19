package com.practicum.playlistmaker.media.ui.playlist

import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.search.domain.Playlist
class MediaViewHolderPlaylist(private val binding: PlaylistItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model : Playlist) = with(binding) {
        playlistTitle.text = model.playlistName
        playlistTracks.text = countTracks(model.playlistCount)
        val context = itemView.context
        val trackImage = model.playlistImage
        val trackUrl = binding.playlistImage
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