package com.study.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.model.Track
import com.study.playlistmaker.search.ui.model.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private var searchText: String = ""
    private var lastQuery: String = ""

    private var searchJob: Job? = null

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var isClickOnTrackAllowed = true

    fun onSearchTextChanged(text: String) {
        searchText = text
        if (searchText.isEmpty()) {
            clearSearch()
        } else {
            lastQuery = searchText
            searchDebounce()
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
        searchDebounce()
    }

    fun retryLastSearch() {
        if (lastQuery.isNotEmpty()) {
            searchDebounce()
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
        searchJob?.cancel()
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

        viewModelScope.launch {
            searchInteractor
                .searchTracks(query)
                .collect {
                    when {
                        it == null -> {
                            _searchState.postValue(SearchState.Error.Network)
                        }
                        it.isEmpty() -> {
                            _searchState.postValue(SearchState.Error.EmptyResult)
                        }
                        else -> {
                            _searchState.postValue(SearchState.Content(it))
                        }
                    }
                }
        }
    }

    private fun searchDebounce() {
        if (searchText.isNotEmpty()) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                performSearchRequest(searchText)
            }
        }
    }

    private fun debounceTrackClick(): Boolean {
        val current = isClickOnTrackAllowed
        if (isClickOnTrackAllowed) {
            isClickOnTrackAllowed = false
            viewModelScope.launch {
                delay(TRACK_CLICK_DEBOUNCE_DELAY)
                isClickOnTrackAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1_000L
    }
}
