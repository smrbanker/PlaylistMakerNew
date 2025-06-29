package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

    private val trackList = ArrayList<Track>()
    val searchAdapter = SearchAdapter(trackList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        notFoundButton = findViewById<ImageView>(R.id.search_image_not_found)
        imageWrongButton = findViewById<ImageView>(R.id.search_image_wrong)
        notFoundText = findViewById<TextView>(R.id.search_text_not_found)
        wrongButton = findViewById<Button>(R.id.search_button_wrong)

        val searchBack =
            findViewById<ImageView>(R.id.button_back2) // возврат на главный экран
        searchBack.setOnClickListener {
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.search_button_visible)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(inputEditText)
            notFoundButton.visibility = View.GONE
            imageWrongButton.visibility = View.GONE
            notFoundText.visibility = View.GONE
            wrongButton.visibility = View.GONE
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

        /*val trackList = listOf(
            Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
            Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
            Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
            Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
            Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
        )*/

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                iAPICall(inputEditText)
                true
            }
            false
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

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
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
            iTunesService.search(text = inputEditText.text.toString()).enqueue(object :
                Callback<iTinesResponse> {
                override fun onResponse(call: Call<iTinesResponse>,
                                        response: Response<iTinesResponse>
                ) {
                    if (response.code() == 200) {
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
}