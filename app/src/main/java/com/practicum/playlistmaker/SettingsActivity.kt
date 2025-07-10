package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.net.toUri
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val image4 = findViewById<ImageView>(R.id.button_back)

        image4.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<ImageView>(R.id.share_button)

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            startActivity(shareIntent)
        }

        val supportButton = findViewById<ImageView>(R.id.support_button)

        supportButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = "mailto:".toUri()
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_mail)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
            startActivity(shareIntent)
        }

        val agreementButton = findViewById<ImageView>(R.id.agreement_button)

        agreementButton.setOnClickListener {
            val address = getString(R.string.agreement_link).toUri()
            val shareIntent = Intent(Intent.ACTION_VIEW, address)
            startActivity(shareIntent)
        }

        val themeSwitcher = findViewById<Switch>(R.id.themeSwitcher)
        val sharedPrefs = getSharedPreferences(PM_PREFERENCES, MODE_PRIVATE)

        themeSwitcher.isChecked = sharedPrefs.getBoolean(SWITCH_KEY, false)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            sharedPrefs.edit {
                putBoolean(SWITCH_KEY, checked)
            }
            (applicationContext as App).switchTheme(checked)
        }
    }
}