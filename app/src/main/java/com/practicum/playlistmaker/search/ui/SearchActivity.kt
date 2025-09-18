package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var saveTrack : LinearLayout
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyView: RecyclerView
    private lateinit var tracksInteractor: TracksInteractor
    private val trackList = ArrayList<Track>()
    val historyListID = mutableListOf<Track>() // список "прокликанных" треков
    private lateinit var simpleTextWatcher : TextWatcher
    private var viewModel: SearchViewModel? = null
    private var viewModelHistory: HistoryViewModel? = null
    private lateinit var inputEditText : EditText
    private val handler = Handler(Looper.getMainLooper())

    val searchAdapter = SearchAdapter(trackList, onTrackClick = { trackID ->
        callPlayerActivity(trackID)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this, SearchViewModel.getFactory(1))
            .get(SearchViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            render(it)
        }
        viewModel?.observeShowToast()?.observe(this) {
            showToast(it)
        }

        viewModelHistory = ViewModelProvider(this, HistoryViewModel.getFactory(1))
            .get(HistoryViewModel::class.java)

        var trackListSP: Array<Track> = viewModelHistory?.trackRead()!!
        historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
            if (viewModel?.clickDebounce() ?: false) {
                callPlayerActivity(trackID)
            }
        })

        saveTrack = binding.searchHistory
        inputEditText = binding.inputEditText

        if (trackListSP.isEmpty()) {
            saveTrack.visibility = View.GONE
        }

        binding.buttonBack2.setOnClickListener { // возврат на главный экран
            viewModelHistory?.trackWrite(historyListID)
            finish()
        }

        binding.searchButtonVisible.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(inputEditText)
            binding.searchImageNotFound.visibility = View.GONE
            binding.searchImageWrong.visibility = View.GONE
            binding.searchTextNotFound.visibility = View.GONE
            binding.searchButtonWrong.visibility = View.GONE

            viewModelHistory?.trackWrite(historyListID)
            trackListSP = viewModelHistory?.trackRead()!!
            historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
                if (viewModel?.clickDebounce() ?: false) {
                    callPlayerActivity(trackID)
                }
            })
            historyView = binding.recyclerViewHistory
            historyView.adapter = historyAdapter
            historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            if (trackListSP.isEmpty()) {
                saveTrack.visibility = View.GONE
            } else {
                saveTrack.visibility = View.VISIBLE
            }
        }

        binding.searchButtonClear.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(inputEditText)
            binding.searchImageNotFound.visibility = View.GONE
            binding.searchImageWrong.visibility = View.GONE
            binding.searchTextNotFound.visibility = View.GONE
            binding.searchButtonWrong.visibility = View.GONE

            viewModelHistory?.trackClear()
            trackListSP = viewModelHistory?.trackRead()!!
            historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
                if (viewModel?.clickDebounce() ?: false) {
                    callPlayerActivity(trackID)
                }
            })
            historyView = binding.recyclerViewHistory
            historyView.adapter = historyAdapter
            historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            saveTrack.visibility = View.GONE
        }

        simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchButtonVisible.visibility = clearButtonVisibility(s)
                trackList.clear()
                searchAdapter.notifyDataSetChanged()
                textFromEdit = s.toString()

                saveTrack.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true && trackListSP.isNotEmpty()) View.VISIBLE else View.GONE

                viewModel?.searchDebounce(inputEditText)
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
                viewModel?.iAPICall(inputEditText)
                true
            }
            false
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            saveTrack.visibility = if (hasFocus && inputEditText.text.isEmpty() && trackListSP.isNotEmpty()) View.VISIBLE else View.GONE
        }

        binding.searchButtonWrong.setOnClickListener {
            binding.searchImageWrong.visibility = View.GONE
            binding.searchTextNotFound.visibility = View.GONE
            binding.searchButtonWrong.visibility = View.GONE
            viewModel?.iAPICall(inputEditText)
        }

        val recyclerView = binding.recyclerView
        recyclerView.adapter = searchAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        historyView = binding.recyclerViewHistory
        historyView.adapter = historyAdapter
        historyView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        tracksInteractor = Creator.provideTracksInteractor(this)
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

    fun callPlayerActivity (trackID : Track) {
        historyListID.add(trackID)
        val trackJson: String = Gson().toJson(trackID)
        val displayIntent = Intent(this, PlayerActivity::class.java)
        displayIntent.putExtra("extra", trackJson)
        startActivity(displayIntent)
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty()
        }
    }

    fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    fun showContent(trackListNew: List<Track>) {
        trackList.clear()
        trackList.addAll(trackListNew)

        val postRunnable = Runnable {
            binding.progressBar.visibility = View.GONE
            searchAdapter.notifyDataSetChanged()
        }
        handler.post(postRunnable)
    }

    fun showError(errorMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.searchImageNotFound.visibility = View.GONE
        binding.searchTextNotFound.visibility = View.VISIBLE
        binding.searchImageWrong.visibility = View.VISIBLE
        binding.searchButtonWrong.visibility = View.VISIBLE
        if (errorMessage == "-1") {
            binding.searchTextNotFound.text = getString(R.string.something_went_wrong)
        } else {
            binding.searchTextNotFound.text = errorMessage
        }
    }

    fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.searchImageNotFound.visibility = View.VISIBLE
        binding.searchTextNotFound.visibility = View.VISIBLE
        binding.searchImageWrong.visibility = View.GONE
        binding.searchButtonWrong.visibility = View.GONE
        binding.searchTextNotFound.text = getString(R.string.nothing_found)
    }

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
    }
}