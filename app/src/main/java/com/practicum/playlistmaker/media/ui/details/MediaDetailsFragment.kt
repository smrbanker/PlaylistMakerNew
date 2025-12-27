package com.practicum.playlistmaker.media.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.media.ui.create.MediaCreatePlaylistFragment

class MediaDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    val viewModel by viewModel<MediaViewModelDetails>()
    private var adapter: MediaDetailsAdapter? = null
    private lateinit var playlistList: RecyclerView
    private val trackList = ArrayList<Track>()
    private lateinit var playlistClicked : Playlist

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistInJson = requireArguments().getString(EXTRA) ?: ""
        val gson : Gson by inject()
        playlistClicked = gson.fromJson(playlistInJson, Playlist::class.java)

        adapter = MediaDetailsAdapter(trackList,
            onTrackClick = { trackID -> callPlayerActivity(trackID) },
            onLongTrackClick = { showDialog(it) }
        )

        playlistList = binding.detailsTrackList
        playlistList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        playlistList.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeState2().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeState3().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeState4().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getDuration(playlistClicked.playlistId!!)

        if (playlistClicked.playlistCount > 0) {
            viewModel.fillData(playlistClicked.playlistId!!)
            binding.detailsEmptyList.visibility = View.GONE
        }
        else {
            playlistList.visibility = View.GONE
            binding.detailsEmptyList.visibility = View.VISIBLE
        }

        binding.detailsBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonShare.setOnClickListener {
            if (binding.detailsEmptyList.isVisible) Toast.makeText(requireContext(), R.string.empty_list, Toast.LENGTH_SHORT).show()
            else viewModel.shareInfo(playlistClicked.playlistId!!)
        }

        val bottomSheetContainer = binding.detailsBottomSheetMenu
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                    else -> {  binding.overlay.visibility = View.VISIBLE  }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })

        binding.buttonMenu.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            Glide.with(binding.details)
                .load(playlistClicked.playlistImage)
                .placeholder(R.drawable.placeholder_large)
                .centerCrop()
                .into(binding.searchTrackImage)

            binding.playlistName.text = playlistClicked.playlistName
            binding.trackCount.text = resources.getQuantityString(
                R.plurals.numberOfTracks,
                playlistClicked.playlistCount,
                playlistClicked.playlistCount
            )
        }

        binding.playlistShare.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (binding.detailsEmptyList.isVisible) Toast.makeText(requireContext(), R.string.empty_list, Toast.LENGTH_SHORT).show()
            else viewModel.shareInfo(playlistClicked.playlistId!!)
        }

        binding.playlistEdit.setOnClickListener {
            val playlistJson: String = gson.toJson(playlistClicked)
            findNavController().navigate(R.id.action_mediaDetailsFragment_to_mediaCreatePlaylistFragment,
                MediaCreatePlaylistFragment.createArgs(playlistJson)
            )
        }

        binding.playlistDelete.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDialogDelete(playlistClicked.playlistId, playlistClicked.playlistName)
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        viewModel.updatePlaylistView(playlistClicked.playlistId!!)
    }

    private fun render(state: MediaStateDetails) {
        when (state) {
            is MediaStateDetails.Content -> showContent(state.long)
            is MediaStateDetails.ContentTracks -> showContentTracks(state.tracks)
            is MediaStateDetails.ContentPlaylist -> showContentPlaylist(state.count)
            is MediaStateDetails.ContentPlaylistView -> showContentPlaylistView(state.playlist)
        }
    }

    private fun showContent(long : Int) {
        val duration = resources.getQuantityString(
            R.plurals.longOfTracks,
            long,
            long
        )
        binding.detailsDuration.text = duration

        val tracks = resources.getQuantityString(
            R.plurals.numberOfTracks,
            playlistClicked.playlistCount,
            playlistClicked.playlistCount
        )
        binding.detailsCountTracks.text = tracks
    }

    private fun showContentPlaylist(playlistCount: Int) {

        val tracks = resources.getQuantityString(
            R.plurals.numberOfTracks,
            playlistCount,
            playlistCount
        )

        binding.detailsCountTracks.text = tracks

        if (playlistCount == 0) {
            playlistList.visibility = View.GONE
            binding.detailsEmptyList.visibility = View.VISIBLE
        }

        binding.overlay.visibility = View.GONE
    }

    private fun showContentTracks(tracks: List<Track>) {
        playlistList.visibility = View.VISIBLE

        trackList.clear()
        trackList.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    private fun showContentPlaylistView(playlist: Playlist) {

        Glide.with(binding.details)
            .load(playlist.playlistImage)
            .placeholder(R.drawable.placeholder_large)
            .centerCrop()
            .into(binding.detailsImage)

        binding.detailsName.text = playlist.playlistName
        binding.detailsDescription.text = playlist.playlistDescription
    }

    fun callPlayerActivity (trackID : Track) {
        val gson : Gson by inject()
        val trackJson: String = gson.toJson(trackID)
        findNavController().navigate(
            R.id.action_mediaDetailsFragment_to_playerFragment,
            PlayerFragment.Companion.createArgs(trackJson))
    }

    private fun showDialog(track: Track) : Boolean {
        binding.overlay.visibility = View.VISIBLE
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.track_delete_Q))
            .setNegativeButton(getString(R.string.not_yet)) { dialog, which -> binding.overlay.visibility = View.GONE}
            .setPositiveButton(getString(R.string.yeap)) { dialog, which ->
                trackList.remove(track)
                viewModel.deleteTrack(track.trackId, playlistClicked.playlistId!!)
                adapter?.notifyDataSetChanged()
            }
            .show()

        return true
    }

    private fun showDialogDelete(playlistID : Int?, playlistName : String)  {
        val string = getString(R.string.playlist_delete_Q) // + " \"" + playlistName + "\"?"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(string)
            .setMessage(getString(R.string.go_out_delete))
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, which -> }
            .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                viewModel.deletePlaylist(playlistClicked.playlistId!!)
                findNavController().navigateUp()
            }
            .show()
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