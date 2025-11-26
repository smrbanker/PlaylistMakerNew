package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuButton.setOnClickListener {
            viewModel.pausePlayer()
            viewModel.resetInfo()
            viewModel.onDestroy()
            findNavController().navigateUp()
        }

        val trackInJson = requireArguments().getString(EXTRA) ?: ""
        val gson : Gson by inject()
        val trackReady = gson.fromJson(trackInJson, Track::class.java)

        val itemView = binding.itemView
        val songLogo = binding.cover

        val image: String = trackReady.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(itemView)
            .load(image)
            .placeholder(R.drawable.placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(songLogo)

        binding.songname.text = trackReady.trackName
        binding.groupname.text = trackReady.artistName
        binding.longtime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackReady.trackTime)
        binding.albumName.text = trackReady.collectionName
        binding.albumYear.text = trackReady.releaseDate.substringBefore('-')
        binding.albumGenre.text = trackReady.primaryGenreName
        binding.albumCountry.text = trackReady.country

        val play = binding.playbutton
        val favour = binding.likebutton
        val timeRec = binding.time
        val url: String = trackReady.previewUrl

        viewModel.checkFavourite(trackReady.trackId)

        viewModel.observePlayerStateInfo().observe(viewLifecycleOwner) {
            when (it.state) {
                PlayerViewModel.STATE_PLAYING -> play.setImageResource(R.drawable.pause_button)
                PlayerViewModel.STATE_PAUSED -> play.setImageResource(R.drawable.play_button)
                PlayerViewModel.STATE_PREPARED -> play.setImageResource(R.drawable.play_button)
                PlayerViewModel.STATE_COMPLETE -> play.setImageResource(R.drawable.play_button)
            }
            timeRec.text = it.time
        }

        viewModel.observeFavouriteInfo().observe(viewLifecycleOwner) {
            when (it) {
                true -> favour.setImageResource(R.drawable.like_button_on)
                false -> favour.setImageResource(R.drawable.like_button_off)
            }
        }

        viewModel.preparePlayer(url)

        play.setOnClickListener {
            playbackControl()
        }

        favour.setOnClickListener {
            viewModel.changeFavourite(trackReady)
        }
    }

    private fun playbackControl() {
        when (viewModel.playerStateInfo.value?.state) {
            PlayerViewModel.STATE_PLAYING -> viewModel.pausePlayer()
            PlayerViewModel.STATE_PREPARED -> viewModel.startPlayer()
            PlayerViewModel.STATE_PAUSED -> viewModel.startPlayer()
            PlayerViewModel.STATE_COMPLETE -> viewModel.startPlayer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EXTRA = "extra"
        fun createArgs(extra: String): Bundle = bundleOf(EXTRA to extra)
    }
}