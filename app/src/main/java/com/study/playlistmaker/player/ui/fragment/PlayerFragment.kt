package com.study.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.FragmentPlayerBinding
import com.study.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.study.playlistmaker.player.ui.adapter.PlaylistsBottomSheetAdapter
import com.study.playlistmaker.player.ui.model.PlayerScreenState
import com.study.playlistmaker.player.ui.model.PlayerTrack
import com.study.playlistmaker.player.ui.view_model.PlayerViewModel
import com.study.playlistmaker.utils.dpToPx
import com.study.playlistmaker.utils.formatMsToDuration
import com.study.playlistmaker.utils.showToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistsAdapter: PlaylistsBottomSheetAdapter
    private lateinit var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val gson: Gson by inject()
    private val args: PlayerFragmentArgs by navArgs()
    private val playerViewModel: PlayerViewModel by viewModel {
        parametersOf(
            gson.fromJson(args.track, PlayerTrack::class.java)
        )
    }
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        playerViewModel.screenState.observe(viewLifecycleOwner) {
            render(it)
        }

        playlistsAdapter = PlaylistsBottomSheetAdapter(
            playlists = mutableListOf(),
            onPlaylistClickListener = { playlist ->
                playerViewModel.addTrackToPlaylist(playlist).observe(viewLifecycleOwner) { trackAlreadyAdded ->
                    if (trackAlreadyAdded) {
                        showToast(requireContext(), requireContext().getString(R.string.track_contains_in_playlist, playlist.name))
                    } else {
                        showToast(requireContext(), requireContext().getString(R.string.track_added_to_playlist, playlist.name))
                        playlistsViewModel.getPlaylists()
                    }
                }
                playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        )
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        playlistsViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistsAdapter.updateData(playlists)
        }

        playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                        }
                        else -> {
                            binding.overlay.isVisible = true
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

    }

    override fun onResume() {
        super.onResume()

        playlistsViewModel.getPlaylists()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.onDestroy()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.playPauseButton.setOnClickListener {
            playerViewModel.togglePlayback()
        }
        binding.addToFavoritesButton.setOnClickListener {
            playerViewModel.onFavoriteClicked()
        }
        binding.addToPlaylistButton.setOnClickListener {
            playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.overlay.setOnClickListener {
            playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun render(state: PlayerScreenState) {
        with(binding) {
            name.text = state.track.trackName
            artist.text = state.track.artistName
            currentPosition.text = formatMsToDuration(state.currentPosition.toLong())
            durationValue.text = formatMsToDuration(state.track.trackTimeMillis)

            if (state.isFavorite) {
                addToFavoritesButton.setImageResource(R.drawable.ic_add_to_favorites_enabled)
            } else {
                addToFavoritesButton.setImageResource(R.drawable.ic_add_to_favorites_disabled)
            }

            Glide.with(requireContext())
                .load(state.track.getCoverArtwork())
                .placeholder(R.drawable.track_artwork_player_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(2f.dpToPx(requireContext()))
                )
                .into(cover)

            val isAlbumAvailable = state.track.collectionName != null
            albumTitle.isVisible = isAlbumAvailable
            albumValue.isVisible = isAlbumAvailable
            albumValue.text = state.track.collectionName
            yearValue.text = state.track.releaseDate.substringBefore('-')
            genreValue.text = state.track.primaryGenreName
            countryValue.text = state.track.country

            playPauseButton.isEnabled = state.isPlayButtonEnabled
            playPauseButton.setBackgroundResource(state.playButtonBackground)
        }
    }
}
