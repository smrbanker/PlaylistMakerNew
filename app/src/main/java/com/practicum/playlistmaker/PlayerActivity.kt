package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import okhttp3.internal.wait
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()     //проигрыватель
    private lateinit var play: ImageView        //кнопка play/stop
    private lateinit var timeRec: TextView      //переменная для секундомера

    private var playerState = STATE_DEFAULT     //переменная для сохранения статуса проигрывателя

    private var mainThreadHandler: Handler? = null //handler для обратного отсчета

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val trackInJson : String? = intent.getStringExtra("extra")
        val trackReady = Gson().fromJson(trackInJson, Track::class.java)

        val itemView: View = findViewById(R.id.player)
        val songLogo: ImageView = findViewById(R.id.cover)
        val image: String? = trackReady.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
        Glide.with(itemView)
            .load(image)
            .placeholder(R.drawable.placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(songLogo)

        val name : TextView = findViewById(R.id.songname)
        name.text = trackReady.trackName

        val artist : TextView = findViewById(R.id.groupname)
        artist.text = trackReady.artistName

        val time : TextView = findViewById(R.id.longtime)
        time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackReady.trackTime)

        val album : TextView = findViewById(R.id.album_name)
        album.text = trackReady.collectionName

        val year : TextView = findViewById(R.id.album_year)
        year.text = trackReady.releaseDate.substringBefore('-').toString()

        val genre : TextView = findViewById(R.id.album_genre)
        genre.text = trackReady.primaryGenreName

        val country: TextView = findViewById(R.id.album_country)
        country.text = trackReady.country

        timeRec = findViewById(R.id.time)

        val buttonBack = findViewById<ImageView>(R.id.menu_button)  //кнопка назад и ее слушатель
        buttonBack.setOnClickListener {
            mainThreadHandler?.removeCallbacksAndMessages(null)
            finish()
        }

        val url : String = trackReady.previewUrl
        mainThreadHandler = Handler(Looper.getMainLooper())

        play = findViewById(R.id.playbutton)
        preparePlayer(url)

        play.setOnClickListener {
            playbackControl()

            if (playerState == STATE_PLAYING) {
                mainThreadHandler?.post {
                    timeRec.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                    mainThreadHandler?.postDelayed(
                        object : Runnable {
                            override fun run() {
                                timeRec.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                                mainThreadHandler?.postDelayed(this,DELAY)
                            }
                        },DELAY)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mainThreadHandler?.removeCallbacksAndMessages(null)
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        play.setImageResource(R.drawable.play_button)
        mainThreadHandler?.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun preparePlayer(url:String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacksAndMessages(null)
            mainThreadHandler?.post { timeRec.text = "00:00" }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    companion object {
        private const val STATE_DEFAULT = 0     //статусы для проигрывателя
        private const val STATE_PREPARED = 1    //статусы для проигрывателя
        private const val STATE_PLAYING = 2     //статусы для проигрывателя
        private const val STATE_PAUSED = 3      //статусы для проигрывателя
        private const val DELAY = 300L          //задержка для обновления секундомера проигрывателя
    }
}