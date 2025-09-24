package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.itemView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.menuButton.setOnClickListener {
            viewModel.pausePlayer()
            viewModel.resetInfo()
            viewModel.onDestroy()
            finish()
        }

        val trackInJson: String? = intent.getStringExtra("extra")
        val gson : Gson by inject()
        val trackReady = gson.fromJson(trackInJson, Track::class.java)

        val itemView = binding.itemView
        val songLogo = binding.cover

        val image: String? = trackReady.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
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
        val timeRec = binding.time
        val url: String = trackReady.previewUrl

        viewModel.observePlayerStateInfo().observe(this) {
            when (it.state) {
                PlayerViewModel.STATE_PLAYING -> play.setImageResource(R.drawable.pause_button)
                PlayerViewModel.STATE_PAUSED -> play.setImageResource(R.drawable.play_button)
                PlayerViewModel.STATE_PREPARED -> play.setImageResource(R.drawable.play_button)
                PlayerViewModel.STATE_COMPLETE -> play.setImageResource(R.drawable.play_button)
            }
            timeRec.text = it.time
        }

        viewModel.preparePlayer(url)

        play.setOnClickListener {
            playbackControl()
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
}