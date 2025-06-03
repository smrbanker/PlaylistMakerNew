package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dz)  //homework
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dz)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val image1 = findViewById<Button>(R.id.button1)

        //закомментированный блок (6 строк) - это Toast, реализованный через анонимный класс
        /*val imageClickListener1: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку1!", Toast.LENGTH_SHORT).show()
            }
        }
        image1.setOnClickListener(imageClickListener1)*/

        image1.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        val image2 = findViewById<Button>(R.id.button2)

        image2.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на кнопку2!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }

        val image3 = findViewById<Button>(R.id.button3)

        image3.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на кнопку3!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

        /*setContentView(R.layout.activity_test2)

        val displayButton = findViewById<Button>(R.id.display)
        val shareButton = findViewById<Button>(R.id.share)

        displayButton.setOnClickListener {
            val displayIntent = Intent(this, MessageActivity::class.java)
            startActivity(displayIntent)
        }

        shareButton.setOnClickListener {
            val message = "Привет, Android разработка — это круто!"
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("yourEmail@ya.ru"))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }*/

    }
}