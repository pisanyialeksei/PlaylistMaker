package com.study.playlistmaker.search.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.study.playlistmaker.databinding.ActivitySearchBinding
import com.study.playlistmaker.player.ui.navigation.PlayerNavigator
import com.study.playlistmaker.search.ui.adapter.TracksAdapter
import com.study.playlistmaker.search.ui.model.SearchState
import com.study.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter

    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    private fun setupAdapters() {
        searchAdapter = TracksAdapter(
            trackList = mutableListOf(),
            clickListener = { track ->
                searchViewModel.onTrackClick(track)
                startActivity(PlayerNavigator.createPlayerIntent(track.toUiTrack(), this))
            }
        )
        binding.searchRecyclerView.adapter = searchAdapter

        historyAdapter = TracksAdapter(
            trackList = mutableListOf(),
            clickListener = { track ->
                searchViewModel.onTrackClick(track)
                startActivity(PlayerNavigator.createPlayerIntent(track.toUiTrack(), this))
            }
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
            searchViewModel.clearHistory()
        }

        binding.searchEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                binding.searchEditTextClear.isVisible = !text.isNullOrEmpty()
                searchViewModel.onSearchTextChanged(text.toString())
            }
        )

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            searchViewModel.onSearchFocusChanged(hasFocus)
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchViewModel.performManualSearch()
            }
            false
        }
        binding.searchNetworkErrorRefreshButton.setOnClickListener {
            searchViewModel.retryLastSearch()
        }
    }

    private fun observeViewModel() {
        searchViewModel.searchState.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        with(binding) {
            progressBar.isVisible = state is SearchState.Loading
            searchNetworkError.isVisible = state is SearchState.Error.Network
            emptySearchResultError.isVisible = state is SearchState.Error.EmptyResult
            history.isVisible = state is SearchState.History
            searchRecyclerView.isVisible = state is SearchState.Content

            when (state) {
                is SearchState.Content -> {
                    searchAdapter.updateData(state.tracks)
                }

                is SearchState.History -> {
                    historyAdapter.updateData(state.tracks)
                }

                else -> {}
            }
        }
    }
}
