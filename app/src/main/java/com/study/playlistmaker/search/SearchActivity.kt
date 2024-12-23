package com.study.playlistmaker.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.study.playlistmaker.PlayerActivity
import com.study.playlistmaker.R
import com.study.playlistmaker.SHARED_PREFERENCES
import com.study.playlistmaker.gson
import com.study.playlistmaker.search.data.RetrofitClient
import com.study.playlistmaker.search.data.SearchResponse
import com.study.playlistmaker.track.Track
import com.study.playlistmaker.track.Track.Companion.TRACK_INTENT_KEY
import com.study.playlistmaker.track.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private var lastQuery: String = ""

    private val searchList = mutableListOf<Track>()
    private lateinit var historyList: MutableList<Track>

    private lateinit var searchHistoryManager: SearchHistoryManager
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var emptyResultError: LinearLayout
    private lateinit var networkError: LinearLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var historyView: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearchRequest(searchText) }

    private var isClickOnTrackAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        searchHistoryManager = SearchHistoryManager(sharedPreferences)
        searchAdapter = TrackAdapter(
            trackList = searchList,
            clickListener = { track -> openPlayerWithTrack(track) }
        )
        historyList = searchHistoryManager.currentHistory
        historyAdapter = TrackAdapter(
            trackList = historyList,
            clickListener = { track -> openPlayerWithTrack(track) }
        )

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.search_edit_text_clear)
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            val imm = searchEditText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        progressBar = findViewById(R.id.progress_bar)

        searchRecyclerView = findViewById(R.id.search_recycler_view)
        searchRecyclerView.adapter = searchAdapter

        historyView = findViewById(R.id.search_history)
        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
        historyRecyclerView.adapter = historyAdapter

        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            searchHistoryManager.clearHistory()
            historyList.clear()
            historyAdapter.notifyItemRangeRemoved(0, 10)
            historyView.isVisible = false
        }

        emptyResultError = findViewById(R.id.empty_search_result_error)
        networkError = findViewById(R.id.search_network_error)
        val networkErrorRefreshButton = findViewById<Button>(R.id.refresh_button)

        searchEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                clearButton.isVisible = !text.isNullOrEmpty()
                searchText = text.toString()
                if (searchText.isNotEmpty()) {
                    searchDebounce()
                } else {
                    val itemCount = searchList.size
                    searchList.clear()
                    searchAdapter.notifyItemRangeRemoved(0, itemCount)
                    emptyResultError.isVisible = false
                    networkError.isVisible = false
                    if (searchEditText.hasFocus() && searchHistoryManager.currentHistory.isNotEmpty()) {
                        historyList.apply {
                            clear()
                            addAll(searchHistoryManager.currentHistory)
                        }
                        historyAdapter.notifyItemRangeChanged(0, 10)
                        searchRecyclerView.isVisible = false
                        historyView.isVisible = true
                    } else {
                        searchRecyclerView.isVisible = true
                    }
                }
            }
        )
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty() && searchHistoryManager.currentHistory.isNotEmpty()) {
                searchRecyclerView.isVisible = false
                historyView.isVisible = true
                emptyResultError.isVisible = false
                networkError.isVisible = false
            } else {
                searchRecyclerView.isVisible = true
                historyView.isVisible = false
                emptyResultError.isVisible = false
                networkError.isVisible = false
            }
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && searchText.isNotEmpty()) {
                searchDebounce()
                lastQuery = searchText
            }
            false
        }
        networkErrorRefreshButton.setOnClickListener {
            performSearchRequest(lastQuery)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(searchRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.setText(searchText)
    }

    private fun performSearchRequest(searchQuery: String) {
        searchRecyclerView.isVisible = false
        historyView.isVisible = false
        emptyResultError.isVisible = false
        networkError.isVisible = false
        progressBar.isVisible = true

        RetrofitClient.itunesSearchApiService.search(searchQuery).enqueue(
            object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    progressBar.isVisible = false
                    if (response.code() == 200) {
                        searchList.clear()
                        val searchResults = response.body()?.results

                        if (!searchResults.isNullOrEmpty()) {
                            searchList.addAll(searchResults)
                            searchAdapter.notifyDataSetChanged()
                            progressBar.isVisible = false
                            searchRecyclerView.isVisible = true
                        }
                        if (searchList.isEmpty()) {
                            progressBar.isVisible = false
                            emptyResultError.isVisible = true
                        }
                    } else {
                        progressBar.isVisible = false
                        networkError.isVisible = true
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    progressBar.isVisible = false
                    networkError.isVisible = true
                }
            }
        )
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun debounceTrackClick(): Boolean {
        val current = isClickOnTrackAllowed
        if (isClickOnTrackAllowed) {
            isClickOnTrackAllowed = false
            mainThreadHandler.postDelayed({ isClickOnTrackAllowed = true }, TRACK_CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun openPlayerWithTrack(track: Track) {
        if (debounceTrackClick()) {
            searchHistoryManager.addTrackToHistory(track)
            val itemJson = gson.toJson(track)
            startActivity(Intent(this, PlayerActivity::class.java).putExtra(TRACK_INTENT_KEY, itemJson))
        }
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1_000L
    }
}
