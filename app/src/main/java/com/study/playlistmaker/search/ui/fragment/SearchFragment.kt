package com.study.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.study.playlistmaker.databinding.FragmentSearchBinding
import com.study.playlistmaker.player.ui.navigation.PlayerNavigator
import com.study.playlistmaker.search.ui.adapter.TracksAdapter
import com.study.playlistmaker.search.ui.model.SearchState
import com.study.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter

    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    private fun setupAdapters() {
        searchAdapter = TracksAdapter(
            trackList = mutableListOf(),
            clickListener = { track ->
                searchViewModel.onTrackClick(track)
                startActivity(PlayerNavigator.createPlayerIntent(track.toUiTrack(), requireContext()))
            }
        )
        binding.searchRecyclerView.adapter = searchAdapter

        historyAdapter = TracksAdapter(
            trackList = mutableListOf(),
            clickListener = { track ->
                searchViewModel.onTrackClick(track)
                startActivity(PlayerNavigator.createPlayerIntent(track.toUiTrack(), requireContext()))
            }
        )
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {

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
        searchViewModel.searchState.observe(viewLifecycleOwner) { state ->
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

    companion object {

        fun newInstance() = SearchFragment()
    }
}
