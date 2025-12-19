package com.practicum.playlistmaker.media.ui.create

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaCreatePlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    var playlistName : String? = null
    var playlistDescription : String? = null
    var playlistUri : Uri? = null
    var dialog : MaterialAlertDialogBuilder? = null
    val viewModel by viewModel<MediaViewModelCreatePlaylist>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if ((playlistName?.isNotEmpty() == true) and (playlistName?.isNotBlank() == true)) {
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
            if((playlistUri != null) or ((playlistName?.isNotEmpty() == true) and (playlistName?.isNotBlank() == true)) or (playlistDescription?.isNotEmpty() == true )) {
                dialog?.show()
            } else {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if ((playlistUri != null) or ((playlistName?.isNotEmpty() == true) and (playlistName?.isNotBlank() == true)) or (playlistDescription?.isNotEmpty() == true)) {
                    dialog?.show()
                } else {
                    findNavController().navigateUp()
                }
            }
        })

        binding.createPlaylistButton.setOnClickListener {
            if ((playlistName?.isNotEmpty() == true) and (playlistName?.isNotBlank() == true)) {
                if(playlistUri != null) {
                    lifecycleScope.launch {
                        viewModel.insertPlaylist(playlistName!!, playlistDescription, playlistUri.toString())
                    }
                }
                else {
                    lifecycleScope.launch {
                        viewModel.insertPlaylist(
                            playlistName!!,
                            playlistDescription,
                            null
                        )
                    }
                }
                Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialog = null
    }
}
