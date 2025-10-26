package com.practicum.playlistmaker.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var saveTrack : LinearLayout
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyView: RecyclerView
    private lateinit var trackListSP: Array<Track>
    private val trackList = ArrayList<Track>()
    var historyListID = mutableListOf<Track>() // список "прокликанных" треков
    private lateinit var simpleTextWatcher : TextWatcher
    private val viewModel by viewModel<SearchViewModel>()
    private val viewModelHistory by viewModel<HistoryViewModel>() //: HistoryViewModel? = null
    private lateinit var inputEditText : EditText
    private val handler = Handler(Looper.getMainLooper())

    val searchAdapter = SearchAdapter(trackList, onTrackClick = { trackID ->
        callPlayerActivity(trackID)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        trackListSP = viewModelHistory.trackRead()
        historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
            if (viewModel.clickDebounce()) {
                callPlayerActivity(trackID)
            }
        })

        saveTrack = binding.searchHistory
        inputEditText = binding.inputEditText

        if (trackListSP.isEmpty()) {
            saveTrack.visibility = View.GONE
        }

        binding.searchButtonVisible.setOnClickListener {
            inputEditText.setText("")
            showHistory()
        }

        binding.searchButtonClear.setOnClickListener {
            inputEditText.setText("")
            viewModelHistory.trackClear()
            showHistory()
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

                viewModel.searchDebounce(inputEditText)
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
                viewModel.iAPICall(inputEditText)
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
            viewModel.iAPICall(inputEditText)
        }

        val recyclerView = binding.recyclerView
        recyclerView.adapter = searchAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        historyView = binding.recyclerViewHistory
        historyView.adapter = historyAdapter
        historyView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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

    fun callPlayerActivity (trackID : Track) {
        historyListID.clear()
        historyListID.add(trackID)
        viewModelHistory.trackWrite(historyListID)
        val gson : Gson by inject()
        val trackJson: String = gson.toJson(trackID)
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
            PlayerFragment.createArgs(trackJson))
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
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

    fun showHistory(){
        //inputEditText.setText("")
        hideSoftKeyboard(inputEditText)
        binding.searchImageNotFound.visibility = View.GONE
        binding.searchImageWrong.visibility = View.GONE
        binding.searchTextNotFound.visibility = View.GONE
        binding.searchButtonWrong.visibility = View.GONE


        trackListSP = viewModelHistory.trackRead()
        historyAdapter = HistoryAdapter(trackListSP, onTrackClick = { trackID ->
            if (viewModel.clickDebounce()) {
                callPlayerActivity(trackID)
            }
        })

        historyView = binding.recyclerViewHistory
        historyView.adapter = historyAdapter
        historyView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        if (trackListSP.isEmpty()) {
            saveTrack.visibility = View.GONE
        } else {
            saveTrack.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        showHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModelHistory.trackWrite(historyListID)
        _binding = null
    }

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
    }
}