package com.practicum.playlistmaker.media.ui.create

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.search.domain.Playlist
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaCreatePlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    var playlistName : String? = null
    var playlistDescription : String? = null
    var playlistUri : Uri? = null
    var dialog : MaterialAlertDialogBuilder? = null
    val viewModel by viewModel<MediaViewModelCreatePlaylist>()
    private lateinit var playlistClicked : Playlist

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistInJson = requireArguments().getString(EXTRA) ?: ""
        val gson : Gson by inject()
        playlistClicked = gson.fromJson(playlistInJson, Playlist::class.java)

        if (playlistInJson != "") { viewModel.completeData(playlistClicked) }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.go_out))
            .setMessage(getString(R.string.lost_data))
            .setNeutralButton(getString(R.string.cancel_button)) { dialog, which -> }
            .setNegativeButton(getString(R.string.end_button)) { dialog, which ->
                findNavController().popBackStack()
            }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.posterImage.setImageURI(uri)
                    playlistUri = uri
                }
            }

        binding.posterImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.hintName.doOnTextChanged {
                text, _, _, _ ->
            playlistName = text?.toString()
            if(!playlistName.isNullOrBlank()) {
                @Suppress("DEPRECATION")
                binding.createPlaylistButton.setBackgroundColor(resources.getColor(R.color.blue))
                binding.createPlaylistButton.setBackgroundDrawable(resources.getDrawable(R.drawable.button_pressed))
            } else {
                @Suppress("DEPRECATION")
                binding.createPlaylistButton.setBackgroundColor(resources.getColor(R.color.gray))
                binding.createPlaylistButton.setBackgroundDrawable(resources.getDrawable(R.drawable.button_enabled))
            }
        }

        binding.hintDescription.doOnTextChanged {
                text, _, _, _ -> playlistDescription = text?.toString()
        }

        binding.backButton.setOnClickListener {
            if (playlistInJson == "") {
                if((playlistUri != null) or (!playlistName.isNullOrBlank()) or (playlistDescription?.isNotEmpty() == true )) {
                    dialog?.show()
                } else {
                    findNavController().navigateUp()
                }
            } else {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (playlistInJson == "") {
                    if ((playlistUri != null) or (!playlistName.isNullOrBlank()) or (playlistDescription?.isNotEmpty() == true)) {
                        dialog?.show()
                    } else {
                        findNavController().navigateUp()
                    }
                } else {
                    findNavController().popBackStack()
                }
            }
        })

        binding.createPlaylistButton.setOnClickListener {
            if (playlistInJson == "") {
                if (!playlistName.isNullOrBlank()) {
                    if (playlistUri != null) {
                        lifecycleScope.launch {
                            viewModel.insertPlaylist(playlistName!!, playlistDescription, playlistUri.toString() )
                        }
                    } else {
                        lifecycleScope.launch {
                            viewModel.insertPlaylist(playlistName!!, playlistDescription, null)
                        }
                    }
                    Toast.makeText(requireContext(),"Плейлист $playlistName создан",Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
            } else {
                if (!playlistName.isNullOrBlank()) {
                    if (playlistUri != null) {
                        viewModel.updatePlaylist(playlistClicked, playlistName!!, playlistDescription, playlistUri.toString() )
                    } else {
                        viewModel.updatePlaylist(playlistClicked, playlistName!!, playlistDescription, playlistClicked.playlistImage)
                    }
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun render(state: MediaStateCreate) {
        when (state) {
            is MediaStateCreate.Content -> showContent(state.playlistName, state.playlistDescription, state.playlistImage)
        }
    }

    private fun showContent(name : String, description : String, image : String) {

        Glide.with(binding.createPlaylist)
            .load(image)
            .placeholder(R.drawable.placeholder_large)
            .centerCrop()
            .into(binding.posterImage)

        binding.hintName.text = Editable.Factory.getInstance().newEditable(name)
        binding.hintDescription.text = Editable.Factory.getInstance().newEditable(description)

        binding.titleScreen.text = getString(R.string.edit)
        binding.createPlaylistButton.text = getString(R.string.save)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialog = null
    }

    companion object {
        private const val EXTRA = "extra"
        fun createArgs(extra: String?): Bundle = bundleOf(EXTRA to extra)
    }
}
