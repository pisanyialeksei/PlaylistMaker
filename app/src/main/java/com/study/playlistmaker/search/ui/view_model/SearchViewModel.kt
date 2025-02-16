package com.study.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.model.Track
import com.study.playlistmaker.search.ui.model.SearchState

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private var searchText: String = ""
    private var lastQuery: String = ""

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchDebounce() }

    private var isClickOnTrackAllowed = true

    fun onSearchTextChanged(text: String) {
        searchText = text
        if (text.isEmpty()) {
            clearSearch()
        } else {
            mainThreadHandler.removeCallbacks(searchRunnable)
            mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        if (hasFocus && searchText.isEmpty() && searchInteractor.currentHistory.isNotEmpty()) {
            showHistory()
        }
    }

    fun performManualSearch() {
        if (searchText.isEmpty()) return
        lastQuery = searchText
        cancelPendingSearch()
        performSearchRequest(searchText)
    }

    fun retryLastSearch() {
        if (lastQuery.isNotEmpty()) {
            cancelPendingSearch()
            performSearchRequest(lastQuery)
        }
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
        _searchState.value = SearchState.Empty
    }

    fun onTrackClick(track: Track) {
        if (debounceTrackClick()) {
            searchInteractor.addTrackToHistory(track)
            if (_searchState.value is SearchState.History) {
                showHistory()
            }
        }
    }

    private fun clearSearch() {
        cancelPendingSearch()
        if (searchInteractor.currentHistory.isNotEmpty()) {
            showHistory()
        } else {
            _searchState.value = SearchState.Empty
        }
    }

    private fun showHistory() {
        _searchState.value = SearchState.History(searchInteractor.currentHistory)
    }

    private fun performSearchRequest(query: String) {
        _searchState.value = SearchState.Loading

        searchInteractor.searchTracks(
            query = query,
            consumer = (object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    when {
                        foundTracks == null -> {
                            _searchState.postValue(SearchState.Error.Network)
                        }

                        foundTracks.isEmpty() -> {
                            _searchState.postValue(SearchState.Error.EmptyResult)
                        }

                        else -> {
                            _searchState.postValue(SearchState.Content(foundTracks))
                        }
                    }
                }
            })
        )
    }

    private fun searchDebounce() {
        if (searchText.isNotEmpty()) {
            performSearchRequest(searchText)
        }
    }

    private fun debounceTrackClick(): Boolean {
        val current = isClickOnTrackAllowed
        if (isClickOnTrackAllowed) {
            isClickOnTrackAllowed = false
            mainThreadHandler.postDelayed({ isClickOnTrackAllowed = true }, TRACK_CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun cancelPendingSearch() {
        mainThreadHandler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1_000L
    }
}
