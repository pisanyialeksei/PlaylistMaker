package com.study.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.model.Track
import com.study.playlistmaker.search.ui.model.SearchState
import com.study.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private var searchText: String = ""
    private var lastQuery: String = ""

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val searchDebouncer: (String) -> Unit
    private val trackClickDebouncer: (Track) -> Unit

    init {
        searchDebouncer = debounce<String>(
            delayMillis = SEARCH_DEBOUNCE_DELAY,
            coroutineScope = viewModelScope,
            useLastParam = true
        ) { query ->
            if (query.isNotEmpty() && query == searchText) {
                performSearchRequest(query)
            }
        }

        trackClickDebouncer = debounce<Track>(
            delayMillis = TRACK_CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewModelScope,
            useLastParam = false
        ) { track ->
            searchInteractor.addTrackToHistory(track)
            if (_searchState.value is SearchState.History) {
                showHistory()
            }
        }
    }

    fun onSearchTextChanged(text: String) {
        searchText = text
        if (searchText.isEmpty()) {
            clearSearch()
        } else {
            lastQuery = searchText
            searchDebouncer(searchText)
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
        searchDebouncer(searchText)
    }

    fun retryLastSearch() {
        if (lastQuery.isNotEmpty()) {
            searchDebouncer(lastQuery)
        }
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
        _searchState.value = SearchState.Empty
    }

    fun onTrackClick(track: Track) {
        trackClickDebouncer(track)
    }

    fun updateFavoritesStatus() {
        val currentState = _searchState.value

        if (currentState is SearchState.Content) {
            viewModelScope.launch {
                val favoriteIds = searchInteractor.getFavoriteTrackIds()
                val updatedTracks = currentState.tracks.map { track ->
                    track.apply {
                        isFavorite = favoriteIds.contains(track.trackId)
                    }
                }

                _searchState.postValue(SearchState.Content(updatedTracks))
            }
        } else if (currentState is SearchState.History) {
            viewModelScope.launch {
                val favoriteIds = searchInteractor.getFavoriteTrackIds()
                val updatedTracks = currentState.tracks.map { track ->
                    track.apply {
                        isFavorite = favoriteIds.contains(track.trackId)
                    }
                }
                _searchState.postValue(SearchState.History(updatedTracks))
            }
        }
    }

    private fun clearSearch() {
        if (searchInteractor.currentHistory.isNotEmpty()) {
            showHistory()
        } else {
            _searchState.value = SearchState.Empty
        }
    }

    private fun showHistory() {
        _searchState.value = SearchState.History(searchInteractor.currentHistory)
        updateFavoritesStatus()
    }

    private fun performSearchRequest(query: String) {
        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            searchInteractor
                .searchTracks(query)
                .collect { foundTracks ->
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
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1_000L
    }
}
