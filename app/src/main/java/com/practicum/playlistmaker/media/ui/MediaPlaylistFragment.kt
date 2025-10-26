package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class MediaPlaylistFragment : Fragment() {

    private val playlistViewModel: MediaViewModelPlaylist by viewModel {
        parametersOf(requireArguments().getString(PLAYLIST))
    }

    private lateinit var binding: FragmentMediaPlaylistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistViewModel.observePlaylist().observe(viewLifecycleOwner) {
            binding.searchButtonWrong.visibility = View.GONE
            binding.searchImageNotFound.visibility = View.GONE
            binding.emptyPlaylist.visibility = View.GONE
            binding.emptyPlaylist.text = getString(R.string.empty_playlist)
            showInfo(it)
        }
    }

    private fun showInfo(playlist: String) {
        if (playlist == "") {
            binding.searchButtonWrong.visibility = View.VISIBLE
            binding.searchImageNotFound.visibility = View.VISIBLE
            binding.emptyPlaylist.visibility = View.VISIBLE
        }
        else binding.playlist.text = requireArguments().getString(PLAYLIST).toString()
    }

    companion object {
        private const val PLAYLIST = "playlist"

        fun newInstance(playlist: String) = MediaPlaylistFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLIST, playlist)
            }
        }
    }
}