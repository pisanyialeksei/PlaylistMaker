package com.study.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.study.playlistmaker.databinding.FragmentFavoritesBinding
import com.study.playlistmaker.library.ui.FavoritesState
import com.study.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.study.playlistmaker.search.domain.model.Track
import com.study.playlistmaker.ui.adapter.TracksAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var favoritesAdapter: TracksAdapter

    private val gson: Gson by inject()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.getFavorites()
    }

    private fun setupAdapters() {
        favoritesAdapter = TracksAdapter(
            trackList = mutableListOf(),
            clickListener = { track ->
                navigateToPlayer(track)
            }
        )
        binding.favoritesRecyclerView.adapter = favoritesAdapter
    }

    private fun observeViewModel() {
        favoritesViewModel.screenState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: FavoritesState) {
        with(binding) {
            when (state) {
                is FavoritesState.Content -> {
                    favoritesRecyclerView.isVisible = true
                    emptyFavoritesLinearLayout.isVisible = false
                    favoritesAdapter.updateData(state.tracks)
                }

                is FavoritesState.Empty -> {
                    favoritesRecyclerView.isVisible = false
                    emptyFavoritesLinearLayout.isVisible = true
                }
            }
        }
    }

    private fun navigateToPlayer(track: Track) {
        val navDirection = LibraryFragmentDirections.actionLibraryFragmentToPlayerActivity(
            gson.toJson(track.toUiTrack())
        )
        findNavController().navigate(navDirection)
    }

    companion object {

        fun newInstance() = FavoritesFragment()
    }
}
