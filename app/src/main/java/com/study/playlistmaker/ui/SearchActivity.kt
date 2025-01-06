package com.study.playlistmaker.ui

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
import com.study.playlistmaker.Creator
import com.study.playlistmaker.R
import com.study.playlistmaker.domain.api.history.HistoryInteractor
import com.study.playlistmaker.domain.api.search.SearchInteractor
import com.study.playlistmaker.domain.models.Track
import com.study.playlistmaker.domain.models.Track.Companion.TRACK_INTENT_KEY
import com.study.playlistmaker.gson
import com.study.playlistmaker.presentation.track.TrackAdapter

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private var lastQuery: String = ""

    private val searchList = mutableListOf<Track>()

    private lateinit var toolbar: Toolbar
    private lateinit var searchEditText: EditText
    private lateinit var searchEditTextClearButton: ImageView
    private lateinit var historyView: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var emptyResultError: LinearLayout
    private lateinit var networkError: LinearLayout
    private lateinit var networkErrorRefreshButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var historyInteractor: HistoryInteractor

    private val tracksInteractorImpl = Creator.provideSearchInteractor()

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearchRequest(searchText) }

    private var isClickOnTrackAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initializeViews()

        historyInteractor = Creator.provideHistoryInteractor()

        setupAdapters()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        if (historyView.isVisible) {
            searchEditText.requestFocus()
            historyAdapter.notifyItemRangeChanged(0, 10)
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

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        searchEditTextClearButton = findViewById(R.id.search_edit_text_clear)
        progressBar = findViewById(R.id.progress_bar)
        searchRecyclerView = findViewById(R.id.search_recycler_view)
        historyView = findViewById(R.id.search_history)
        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        emptyResultError = findViewById(R.id.empty_search_result_error)
        networkError = findViewById(R.id.search_network_error)
        networkErrorRefreshButton = findViewById(R.id.refresh_button)
    }

    private fun setupAdapters() {
        searchAdapter = TrackAdapter(
            trackList = searchList,
            clickListener = { track -> openPlayerWithTrack(track) }
        )
        searchRecyclerView.adapter = searchAdapter

        historyAdapter = TrackAdapter(
            trackList = historyInteractor.currentHistory,
            clickListener = { track -> openPlayerWithTrack(track) }
        )
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditTextClearButton.setOnClickListener {
            searchEditText.text.clear()
            val imm = searchEditText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            historyAdapter.notifyItemRangeRemoved(0, 10)
            historyView.isVisible = false
        }

        searchEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                searchEditTextClearButton.isVisible = !text.isNullOrEmpty()
                searchText = text.toString()
                if (searchText.isNotEmpty()) {
                    searchDebounce()
                } else {
                    val itemCount = searchList.size
                    searchList.clear()
                    searchAdapter.notifyItemRangeRemoved(0, itemCount)
                    emptyResultError.isVisible = false
                    networkError.isVisible = false
                    if (searchEditText.hasFocus() && historyInteractor.currentHistory.isNotEmpty()) {
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
            if (hasFocus && searchText.isEmpty() && historyInteractor.currentHistory.isNotEmpty()) {
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

    private fun performSearchRequest(searchQuery: String) {
        searchRecyclerView.isVisible = false
        historyView.isVisible = false
        emptyResultError.isVisible = false
        networkError.isVisible = false
        progressBar.isVisible = true

        tracksInteractorImpl.searchTracks(
            query = searchQuery,
            consumer = (object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    mainThreadHandler.post {
                        progressBar.isVisible = false
                        when {
                            foundTracks == null -> {
                                networkError.isVisible = true
                            }

                            foundTracks.isEmpty() -> {
                                emptyResultError.isVisible = true
                            }

                            else -> {
                                searchList.clear()
                                searchList.addAll(foundTracks)
                                searchAdapter.notifyDataSetChanged()
                                searchRecyclerView.isVisible = true
                            }
                        }
                    }
                }
            })
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
            historyInteractor.addTrackToHistory(track)
            historyAdapter.updateData(historyInteractor.currentHistory)
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
