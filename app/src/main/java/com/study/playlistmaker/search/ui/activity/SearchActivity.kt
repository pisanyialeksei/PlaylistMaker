package com.study.playlistmaker.search.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.study.playlistmaker.PlayerNavigator
import com.study.playlistmaker.R
import com.study.playlistmaker.creator.Creator
import com.study.playlistmaker.databinding.ActivitySearchBinding
import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.model.Track
import com.study.playlistmaker.search.ui.adapter.TracksAdapter

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private var searchText: String = ""
    private var lastQuery: String = ""

    private val searchList = mutableListOf<Track>()

    private lateinit var searchAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter

    private val searchInteractor = Creator.provideSearchInteractor()

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearchRequest(searchText) }

    private var isClickOnTrackAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        if (binding.history.isVisible) {
            binding.searchEditText.requestFocus()
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

    private fun setupAdapters() {
        searchAdapter = TracksAdapter(
            trackList = searchList,
            clickListener = { track -> openPlayerWithTrack(track) }
        )
        binding.searchRecyclerView.adapter = searchAdapter

        historyAdapter = TracksAdapter(
            trackList = searchInteractor.currentHistory,
            clickListener = { track -> openPlayerWithTrack(track) }
        )
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.searchEditTextClear.setOnClickListener {
            binding.searchEditText.text.clear()
            val imm = binding.searchEditText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }

        binding.clearHistoryButton.setOnClickListener {
            searchInteractor.clearHistory()
            historyAdapter.notifyItemRangeRemoved(0, 10)
            binding.history.isVisible = false
        }

        binding.searchEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                binding.searchEditTextClear.isVisible = !text.isNullOrEmpty()
                searchText = text.toString()
                if (searchText.isNotEmpty()) {
                    searchDebounce()
                } else {
                    val itemCount = searchList.size
                    searchList.clear()
                    searchAdapter.notifyItemRangeRemoved(0, itemCount)
                    binding.emptySearchResultError.isVisible = false
                    binding.searchNetworkError.isVisible = false
                    if (binding.searchEditText.hasFocus() && searchInteractor.currentHistory.isNotEmpty()) {
                        historyAdapter.notifyItemRangeChanged(0, 10)
                        binding.searchRecyclerView.isVisible = false
                        binding.history.isVisible = true
                    } else {
                        binding.searchRecyclerView.isVisible = true
                    }
                }
            }
        )
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty() && searchInteractor.currentHistory.isNotEmpty()) {
                binding.searchRecyclerView.isVisible = false
                binding.history.isVisible = true
                binding.emptySearchResultError.isVisible = false
                binding.searchNetworkError.isVisible = false
            } else {
                binding.searchRecyclerView.isVisible = true
                binding.history.isVisible = false
                binding.emptySearchResultError.isVisible = false
                binding.searchNetworkError.isVisible = false
            }
        }
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && searchText.isNotEmpty()) {
                searchDebounce()
                lastQuery = searchText
            }
            false
        }
        binding.searchNetworkErrorRefreshButton.setOnClickListener {
            performSearchRequest(lastQuery)
        }
    }

    private fun performSearchRequest(searchQuery: String) {
        binding.searchRecyclerView.isVisible = false
        binding.history.isVisible = false
        binding.emptySearchResultError.isVisible = false
        binding.searchNetworkError.isVisible = false
        binding.progressBar.isVisible = true

        searchInteractor.searchTracks(
            query = searchQuery,
            consumer = (object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    mainThreadHandler.post {
                        binding.progressBar.isVisible = false
                        when {
                            foundTracks == null -> {
                                binding.searchNetworkError.isVisible = true
                            }
                            foundTracks.isEmpty() -> {
                                binding.emptySearchResultError.isVisible = true
                            }
                            else -> {
                                searchList.clear()
                                searchList.addAll(foundTracks)
                                searchAdapter.notifyDataSetChanged()
                                binding.searchRecyclerView.isVisible = true
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
            searchInteractor.addTrackToHistory(track)
            historyAdapter.updateData(searchInteractor.currentHistory)
            val playerIntent = PlayerNavigator.createPlayerIntent(track, this)
            startActivity(playerIntent)
        }
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1_000L
    }
}
