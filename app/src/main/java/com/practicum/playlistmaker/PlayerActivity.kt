package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

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

        val buttonBack = findViewById<ImageView>(R.id.menu_button)

        buttonBack.setOnClickListener {
            finish()
        }
    }
}