package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaTrackBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaTrackFragment : Fragment() {

    companion object {
        private const val TRACK = "track"

        fun newInstance(track: String) = MediaTrackFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK, track)
            }
        }
    }

    private val trackViewModel: MediaViewModelTrack by viewModel {
        parametersOf(requireArguments().getString(TRACK))
    }

    private lateinit var binding: FragmentMediaTrackBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackViewModel.observeTrack().observe(viewLifecycleOwner) {
            binding.searchImageNotFound.visibility = View.GONE
            binding.emptyMedia.visibility = View.GONE
            binding.emptyMedia.text = getString(R.string.empty_media)
            showInfo(it)
        }
    }

    private fun showInfo(track: String) {
        if (track == "") {
            binding.searchImageNotFound.visibility = View.VISIBLE
            binding.emptyMedia.visibility = View.VISIBLE
        }
        else binding.track.text = requireArguments().getString(TRACK).toString()
    }
}