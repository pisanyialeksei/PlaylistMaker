package com.study.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.FragmentPlaylistBinding
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.library.ui.view_model.PlaylistViewModel
import com.study.playlistmaker.ui.adapter.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private lateinit var tracksAdapter: TracksAdapter

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistFragmentArgs by navArgs()
    private val playlistViewModel: PlaylistViewModel by viewModel {
        parametersOf(args.playlistId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel.playlist.observe(viewLifecycleOwner) {
            render(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun render(playlist: Playlist) {
        with(binding) {
            Glide.with(requireContext())
                .load(playlist.cover)
                .placeholder(R.drawable.track_artwork_player_placeholder)
                .transform(CenterCrop())
                .into(playlistCoverImageView)

            playlistName.text = playlist.name
            playlistDescription.text = playlist.description
            playlistLength.text = "10 min"
            playlistCount.text = requireContext().resources.getQuantityString(
                R.plurals.playlist_tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )

        }
    }

    private fun setupAdapters() {
        tracksAdapter = TracksAdapter(
            trackList = mutableListOf(),
            clickListener = { track ->
                // Handle track click
            }
        )
        binding.playlistTracksRecyclerView.adapter = tracksAdapter
    }
}
