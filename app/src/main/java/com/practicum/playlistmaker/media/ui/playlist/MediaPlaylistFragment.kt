package com.practicum.playlistmaker.media.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaPlaylistBinding
import com.practicum.playlistmaker.search.domain.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaPlaylistFragment : Fragment() {

    private val playlistViewModel: MediaViewModelPlaylist by viewModel {
        parametersOf(requireArguments().getString(PLAYLIST))
    }
    private var _binding: FragmentMediaPlaylistBinding? = null
    private val binding get() = _binding!!
    private var adapter: MediaAdapterPlaylist? = null
    private val playlistList = ArrayList<Playlist>()
    private lateinit var playlistGrid: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMediaPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaFragment_to_mediaCreatePlaylistFragment
                //,PlayerFragment.createArgs(trackJson)
            )
        }

        adapter = MediaAdapterPlaylist(playlistList, onPlaylistClick = { playlistID -> })

        playlistGrid = binding.recyclerView
        playlistGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistGrid.adapter = adapter

        playlistViewModel.fillData()

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        playlistGrid.adapter = null
        _binding = null
    }

    private fun render(state: MediaStatePlaylist) {
        when (state) {
            is MediaStatePlaylist.Content -> showContent(state.playlist)
            is MediaStatePlaylist.Empty -> showEmpty(state.message)
        }
    }

    private fun showEmpty(message: String) {
        binding.newPlaylist.visibility = View.VISIBLE
        binding.searchImageNotFound.visibility = View.VISIBLE
        binding.emptyPlaylist.visibility = View.VISIBLE
        binding.emptyPlaylist.text = getString(R.string.empty_playlist)
    }

    private fun showContent(playlist: List<Playlist>) {
        playlistGrid.visibility = View.VISIBLE
        binding.newPlaylist.visibility = View.VISIBLE
        binding.searchImageNotFound.visibility = View.GONE
        binding.emptyPlaylist.visibility = View.GONE

        playlistList.clear()
        playlistList.addAll(playlist)
        adapter?.notifyDataSetChanged()
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