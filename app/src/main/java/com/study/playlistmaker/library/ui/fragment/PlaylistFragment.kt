package com.study.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        setupBottomSheets()
        setupAdapters()
        setupObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        playlistViewModel.getPlaylist()
        playlistViewModel.getPlaylistTracks()
    }

    private fun setupBottomSheets() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistTracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                            tracksBottomSheetBehavior.isDraggable = true
                        }
                        else -> {
                            binding.overlay.isVisible = true
                            tracksBottomSheetBehavior.isDraggable = false
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupObservers() {
        playlistViewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            renderPlaylistInfo(playlist)
            setupMenuHeader(playlist)
        }

        playlistViewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                binding.emptyPlaylistTextView.isVisible = true
                tracksBottomSheetBehavior.isDraggable = false
            } else {
                renderTracks(tracks)
            }
        }

        playlistViewModel.playlistDuration.observe(viewLifecycleOwner) { duration ->
            binding.playlistLength.text = resources.getQuantityString(
                R.plurals.playlist_duration,
                duration,
                duration,
            )
        }

        playlistViewModel.playlistDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                findNavController().navigateUp()
            }
        }
    }

    private fun renderPlaylistInfo(playlist: Playlist) {
        with(binding) {
            Glide.with(requireContext())
                .load(playlist.cover)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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
            getShareClickListener()
        }

        binding.playlistMenuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.menuShareTextView.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            getShareClickListener()
        }

        binding.menuEditTextView.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val navDirection = PlaylistFragmentDirections.actionPlaylistFragmentToEditPlaylistFragment(
                args.playlistId,
            )
            findNavController().navigate(navDirection)
        }

        binding.menuDeleteTextView.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showPlaylistDeletionDialog()
        }
    }

    private fun showRemoveTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialog)
            .setTitle(getString(R.string.track_deletion_confirmation))
            .setMessage("")
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                playlistViewModel.removeTrackFromPlaylist(track)
                dialog.dismiss()
            }
            .show()
    }

    private fun showPlaylistDeletionDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialog)
            .setTitle(getString(R.string.playlist_deletion_confirmation))
            .setMessage("")
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                playlistViewModel.deletePlaylist()
                dialog.dismiss()
            }
            .show()
    }

    private fun setupMenuHeader(playlist: Playlist) {
        val headerView = binding.menuBottomSheet.findViewById<ConstraintLayout>(R.id.menu_header)

        with(headerView) {
            val coverImageView = findViewById<ImageView>(R.id.bottom_sheet_playlist_item_cover)
            val nameTextView = findViewById<TextView>(R.id.bottom_sheet_playlist_item_name)
            val counterTextView = findViewById<TextView>(R.id.bottom_sheet_playlist_item_counter)

            Glide.with(requireContext())
                .load(playlist.cover)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.track_artwork_list_placeholder)
                .transform(CenterCrop())
                .into(coverImageView)

            nameTextView.text = playlist.name

            counterTextView.text = requireContext().resources.getQuantityString(
                R.plurals.playlist_tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )
        }
    }

    private fun getShareClickListener() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
