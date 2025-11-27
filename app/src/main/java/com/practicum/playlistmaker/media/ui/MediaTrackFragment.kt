package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaTrackBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class MediaTrackFragment : Fragment() {

    private val trackViewModel: MediaViewModelTrack by viewModel {
        parametersOf(requireArguments().getString(TRACK))
    }

    private var _binding: FragmentMediaTrackBinding? = null
    private val binding get() = _binding!!
    private var adapter: MediaAdapter? = null
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var favouriteList: RecyclerView
    private val trackList = ArrayList<Track>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMediaTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MediaAdapter(trackList, onTrackClick = { trackID ->
            callPlayerActivity(trackID)
        })

        imageView = binding.searchImageNotFound
        textView = binding.emptyMedia

        favouriteList = binding.recyclerView
        favouriteList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favouriteList.adapter = adapter

        trackViewModel.fillData()

        trackViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        favouriteList.adapter = null
        _binding = null
    }

    private fun render(state: MediaStateTrack) {
        when (state) {
            is MediaStateTrack.Content -> showContent(state.tracks)
            is MediaStateTrack.Empty -> showEmpty(state.message)
        }
    }

    private fun showEmpty(message: String) {
        favouriteList.visibility = View.GONE
        imageView.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE
        textView.text = message
    }

    private fun showContent(tracks: List<Track>) {
        favouriteList.visibility = View.VISIBLE
        imageView.visibility = View.GONE
        textView.visibility = View.GONE

        trackList.clear()
        trackList.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    fun callPlayerActivity (trackID : Track) {
        val gson : Gson by inject()
        val trackJson: String = gson.toJson(trackID)
        findNavController().navigate(R.id.action_mediaFragment_to_playerFragment,
            PlayerFragment.createArgs(trackJson))
    }

    companion object {
        private const val TRACK = "track"

        fun newInstance(track: String) = MediaTrackFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK, track)
            }
        }
    }
}