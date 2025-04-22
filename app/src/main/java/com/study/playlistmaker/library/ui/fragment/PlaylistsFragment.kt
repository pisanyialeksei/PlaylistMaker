package com.study.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.FragmentPlaylistsBinding
import com.study.playlistmaker.library.ui.PlaylistsState
import com.study.playlistmaker.library.ui.adapter.PlaylistsAdapter
import com.study.playlistmaker.library.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private lateinit var playlistsAdapter: PlaylistsAdapter

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }

        playlistsAdapter = PlaylistsAdapter(
            playlists = mutableListOf(),
            clickListener = { playlist ->
                val navDirection = LibraryFragmentDirections.actionLibraryFragmentToPlaylistFragment(playlist.playlistId)
                findNavController().navigate(navDirection)
            }
        )
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        playlistsViewModel.getPlaylists()
    }

    private fun observeViewModel() {
        playlistsViewModel.screenState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlaylistsState) {
        with(binding) {
            when (state) {
                is PlaylistsState.Content -> {
                    playlistsRecyclerView.isVisible = true
                    emptyPlaylistsLinearLayout.isVisible = false
                    playlistsAdapter.updateData(state.playlists)
                }
                is PlaylistsState.Empty -> {
                    playlistsRecyclerView.isVisible = false
                    emptyPlaylistsLinearLayout.isVisible = true
                }
            }
        }
    }

    companion object {

        fun newInstance() = PlaylistsFragment()
    }
}
