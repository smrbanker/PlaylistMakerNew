package com.practicum.playlistmaker

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
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.hideKeyboard
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val PM_PREFERENCES = "pm_preferences"
const val SAVE_LIST = "save_list"
const val SWITCH_KEY = "key_for_switch"
const val SAVE_KEY = "key_for_save"

class SearchActivity : AppCompatActivity() {

    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesAPI::class.java)

    private lateinit var notFoundButton : ImageView
    private lateinit var imageWrongButton : ImageView
    private lateinit var notFoundText : TextView
    private lateinit var wrongButton : Button
    private lateinit var saveTrack : LinearLayout
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyView: RecyclerView
    private lateinit var searchRunnable: Runnable
    private lateinit var progressBar : ProgressBar

    private val trackList = ArrayList<Track>()
    val historyListID = mutableListOf<Track>() // список "прокликанных" треков

    val searchAdapter = SearchAdapter(trackList, onTrackClick = {trackID ->
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val sp = getSharedPreferences(SAVE_LIST, MODE_PRIVATE)
        val searchHistory = SearchHistory(sp)
        var trackListSP: Array<Track> = searchHistory.read()
        historyAdapter = HistoryAdapter(trackListSP, onTrackClick = {trackID ->
            if (clickDebounce()) { callPlayerActivity(trackID)}     //задержка двойного нажатия
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
            searchHistory.write(searchHistory.add(historyListID))
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.search_button_visible)

        searchRunnable = Runnable { iAPICall(inputEditText) } //запуск поиску по таймеру 2 сек

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(inputEditText)
            notFoundButton.visibility = View.GONE
            imageWrongButton.visibility = View.GONE
            notFoundText.visibility = View.GONE
            wrongButton.visibility = View.GONE

            searchHistory.write(searchHistory.add(historyListID))
            trackListSP = searchHistory.read()
            historyAdapter = HistoryAdapter(trackListSP, onTrackClick = {trackID ->
                if (clickDebounce()) { callPlayerActivity(trackID)}     //задержка двойного нажатия
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
            hideKeyboard(inputEditText)
            notFoundButton.visibility = View.GONE
            imageWrongButton.visibility = View.GONE
            notFoundText.visibility = View.GONE
            wrongButton.visibility = View.GONE

            searchHistory.clear()
            trackListSP = searchHistory.read()
            historyAdapter = HistoryAdapter(trackListSP, onTrackClick = {trackID ->
                if (clickDebounce()) { callPlayerActivity(trackID) }    //задержка двойного нажатия
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
            iTunesService.search(text = inputEditText.text.toString(), text2 = "song").enqueue(object :
                Callback<iTinesResponse> {
                override fun onResponse(call: Call<iTinesResponse>,
                                        response: Response<iTinesResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) { //code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            searchAdapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "")
                        } else {
                            showMessage("", "")
                        }
                    } else {
                        showMessage(getString(R.string.something_went_wrong), response.code().toString())
                    }
                }

                override fun onFailure(call: Call<iTinesResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                }
            })
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

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}