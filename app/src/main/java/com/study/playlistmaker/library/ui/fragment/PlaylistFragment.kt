package com.study.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.FragmentPlaylistBinding
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.library.ui.view_model.PlaylistViewModel
import com.study.playlistmaker.search.domain.model.Track
import com.study.playlistmaker.ui.adapter.TracksAdapter
import com.study.playlistmaker.utils.formatMsToDuration
import com.study.playlistmaker.utils.showToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistFragmentArgs by navArgs()
    private val playlistViewModel: PlaylistViewModel by viewModel {
        parametersOf(args.playlistId)
    }
    private val gson: Gson by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        playlistViewModel.getPlaylistTracks()
    }

    private fun setupObservers() {
        playlistViewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            renderPlaylistInfo(playlist)
        }

        playlistViewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            renderTracks(tracks)
        }

        playlistViewModel.playlistDuration.observe(viewLifecycleOwner) { duration ->
            binding.playlistLength.text = duration
        }
    }

    private fun renderPlaylistInfo(playlist: Playlist) {
        with(binding) {
            Glide.with(requireContext())
                .load(playlist.cover)
                .placeholder(R.drawable.track_artwork_player_placeholder)
                .transform(CenterCrop())
                .into(playlistCoverImageView)

            playlistName.text = playlist.name
            playlistDescription.text = playlist.description
            playlistCount.text = requireContext().resources.getQuantityString(
                R.plurals.playlist_tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )
        }
    }

    private fun renderTracks(tracks: List<Track>) {
        tracksAdapter.updateData(tracks)
    }

    private fun setupAdapters() {
        tracksAdapter = TracksAdapter(
            trackList = mutableListOf(),
            clickListener = { track ->
                val navDirection = PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(
                    gson.toJson(track.toUiTrack())
                )
                findNavController().navigate(navDirection)
            },
            longClickListener = { track ->
                showRemoveTrackDialog(track)
                true
            }
        )
        binding.playlistTracksRecyclerView.adapter = tracksAdapter
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.playlistShareButton.setOnClickListener {
            val tracks = playlistViewModel.playlistTracks.value

            if (tracks.isNullOrEmpty()) {
                showToast(requireContext(), requireContext().getString(R.string.empty_playlist_toast))
            } else {
                val trackListText = tracks.mapIndexed { index, track ->
                    "${index + 1}. ${track.artistName} - ${track.trackName} (${formatMsToDuration(track.trackTimeMillis)})"
                }.joinToString("\n")

                val intentText = """
                    ${binding.playlistName.text}
                    ${binding.playlistDescription.text}
                    ${binding.playlistCount.text}:
                    $trackListText
                    """.trimIndent()

                playlistViewModel.sharePlaylist(intentText)
            }
        }
    }

    private fun showRemoveTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialog)
            .setTitle(getString(R.string.track_deletion_confirmation))
            .setMessage("")
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                playlistViewModel.removeTrackFromPlaylist(track)
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
