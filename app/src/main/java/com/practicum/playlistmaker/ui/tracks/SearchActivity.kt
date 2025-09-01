package com.practicum.playlistmaker.ui.tracks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.SearchHistory
import com.practicum.playlistmaker.data.network.TracksConverterImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.Creator
import com.practicum.playlistmaker.domain.api.TracksInteractor
import android.view.inputmethod.InputMethodManager

const val PM_PREFERENCES = "pm_preferences"
const val SAVE_LIST = "save_list"
const val SWITCH_KEY = "key_for_switch"
const val SAVE_KEY = "key_for_save"

class SearchActivity : AppCompatActivity(), TracksInteractor.TracksConsumer {

    private lateinit var notFoundButton : ImageView
    private lateinit var imageWrongButton : ImageView
    private lateinit var notFoundText : TextView
    private lateinit var wrongButton : Button
    private lateinit var saveTrack : LinearLayout
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyView: RecyclerView
    private lateinit var searchRunnable: Runnable
    private lateinit var progressBar : ProgressBar

    private lateinit var tracksInteractor: TracksInteractor

    private val trackList = ArrayList<Track>()
    val historyListID = mutableListOf<Track>() // список "прокликанных" треков

    val searchAdapter = SearchAdapter(trackList, onTrackClick = { trackID ->
        historyListID.add(trackID)
        val trackJson: String = Gson().toJson(trackID)
        val displayIntent = Intent(this, PlayerActivity::class.java)
        displayIntent.putExtra("extra", trackJson)
        startActivity(displayIntent)
    })

    private var isClickAllowed = true //boolean для clickDebounce двойного нажатия
    private val handler = Handler(Looper.getMainLooper()) //handler для clickDebounce двойного нажатия

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tracksConverter = TracksConverterImpl()
        val sp = getSharedPreferences(SAVE_LIST, MODE_PRIVATE)
        val searchHistory = SearchHistory(sp)
        var trackListSP: Array<Track> = tracksConverter.listConvertFromDto((searchHistory.read()).toList()).toTypedArray()
        historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
            if (clickDebounce()) {
                callPlayerActivity(trackID)
            }     //задержка двойного нажатия
        })

        notFoundButton = findViewById<ImageView>(R.id.search_image_not_found)
        imageWrongButton = findViewById<ImageView>(R.id.search_image_wrong)
        notFoundText = findViewById<TextView>(R.id.search_text_not_found)
        wrongButton = findViewById<Button>(R.id.search_button_wrong)
        saveTrack = findViewById<LinearLayout>(R.id.search_history)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        if (trackListSP.isEmpty()) {
            saveTrack.visibility = View.GONE
        }

        val searchBack = findViewById<ImageView>(R.id.button_back2) // возврат на главный экран
        searchBack.setOnClickListener {
            searchHistory.write(searchHistory.add(tracksConverter.listConvertToDto(historyListID).toMutableList()).toMutableList())
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.search_button_visible)

        searchRunnable = Runnable { iAPICall(inputEditText) } //запуск поиску по таймеру 2 сек

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(inputEditText)
            notFoundButton.visibility = View.GONE
            imageWrongButton.visibility = View.GONE
            notFoundText.visibility = View.GONE
            wrongButton.visibility = View.GONE

            searchHistory.write(searchHistory.add(tracksConverter.listConvertToDto(historyListID).toMutableList()).toMutableList())
            trackListSP = tracksConverter.listConvertFromDto(searchHistory.read().toList()).toTypedArray()
            historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
                if (clickDebounce()) {
                    callPlayerActivity(trackID)
                }     //задержка двойного нажатия
            })
            historyView = findViewById<RecyclerView>(R.id.recyclerViewHistory)
            historyView.adapter = historyAdapter
            historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            if (trackListSP.isEmpty()) {
                saveTrack.visibility = View.GONE
            } else {
                saveTrack.visibility = View.VISIBLE
            }
        }

        val clearHistoryButton = findViewById<Button>(R.id.search_button_clear)
        clearHistoryButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(inputEditText)
            notFoundButton.visibility = View.GONE
            imageWrongButton.visibility = View.GONE
            notFoundText.visibility = View.GONE
            wrongButton.visibility = View.GONE

            searchHistory.clear()
            trackListSP = tracksConverter.listConvertFromDto(searchHistory.read().toList()).toTypedArray()
            historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
                if (clickDebounce()) {
                    callPlayerActivity(trackID)
                }    //задержка двойного нажатия
            })
            historyView = findViewById<RecyclerView>(R.id.recyclerViewHistory)
            historyView.adapter = historyAdapter
            historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            saveTrack.visibility = View.GONE
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                trackList.clear()
                searchAdapter.notifyDataSetChanged()
                textFromEdit = s.toString()

                saveTrack.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true && trackListSP.isNotEmpty()) View.VISIBLE else View.GONE

                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        if (savedInstanceState != null) {
            textFromEdit = savedInstanceState.getString(EDIT_TEXT, TEXT_DEF)
            inputEditText.setText(textFromEdit)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                iAPICall(inputEditText)
                true
            }
            false
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            saveTrack.visibility = if (hasFocus && inputEditText.text.isEmpty() && trackListSP.isNotEmpty()) View.VISIBLE else View.GONE
        }

        wrongButton.setOnClickListener {
            imageWrongButton.visibility = View.GONE
            notFoundText.visibility = View.GONE
            wrongButton.visibility = View.GONE
            iAPICall(inputEditText)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = searchAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        historyView = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        historyView.adapter = historyAdapter
        historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        tracksInteractor = Creator.provideTracksInteractor()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private var textFromEdit: String = TEXT_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, textFromEdit)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textFromEdit = savedInstanceState.getString(EDIT_TEXT, TEXT_DEF)
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            if (text.equals(getString(R.string.nothing_found))) {
                notFoundButton.visibility = View.VISIBLE
                notFoundText.visibility = View.VISIBLE
                imageWrongButton.visibility = View.GONE
                wrongButton.visibility = View.GONE
                notFoundText.text = text
            }
            if (text.equals(getString(R.string.something_went_wrong))) {
                notFoundButton.visibility = View.GONE
                notFoundText.visibility = View.VISIBLE
                imageWrongButton.visibility = View.VISIBLE
                wrongButton.visibility = View.VISIBLE
                notFoundText.text = text
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            notFoundButton.visibility = View.GONE
            imageWrongButton.visibility = View.GONE
            notFoundText.visibility = View.GONE
            wrongButton.visibility = View.GONE
        }
    }

    fun iAPICall (inputEditText : TextView) {
        if (inputEditText.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            searchTracks(inputEditText)
        }
    }

    private fun searchTracks(inputEditText : TextView) {
        tracksInteractor.searchTracks(inputEditText.text.toString(), "song", this)
    }

    override fun consume(foundTracks: List<Track>) {
        trackList.clear()

        if (foundTracks.isNotEmpty()) {
            trackList.addAll(foundTracks)
            val postRunnable = Runnable {
                progressBar.visibility = View.GONE
                searchAdapter.notifyDataSetChanged()
            }
            handler.post(postRunnable)
        } else {
            showMessage(getString(R.string.nothing_found), "")
        }
    }

    fun callPlayerActivity (trackID : Track) {
        historyListID.add(trackID)
        val trackJson: String = Gson().toJson(trackID)
        val displayIntent = Intent(this, PlayerActivity::class.java)
        displayIntent.putExtra("extra", trackJson)
        startActivity(displayIntent)
    }

    private fun clickDebounce() : Boolean {     //задержка для двойного нажатия
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}