package com.study.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.R
import com.study.playlistmaker.library.ui.view_model.EditPlaylistViewModel
import com.study.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : NewPlaylistFragment() {

    private val args: PlaylistFragmentArgs by navArgs()

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(args.playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.edit_playlist_title)
        binding.newPlaylistCreateButton.text = getString(R.string.save)

        observePlaylistData()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.newPlaylistCreateButton.setOnClickListener {
            savePlaylistChanges()
        }
    }

    private fun observePlaylistData() {
        viewModel.playlistCover.observe(viewLifecycleOwner) { coverPath ->
            coverPath?.let { path ->
                val coverFile = File(path)
                if (coverFile.exists()) {
                    coverImageUri = null
                    Glide.with(requireContext())
                        .load(coverFile)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(8f.dpToPx(requireContext()))
                        )
                        .into(binding.playlistCoverImageView)
                }
            }
        }

        viewModel.playlistName.observe(viewLifecycleOwner) { name ->
            if (binding.playlistNameTextInputLayout.editText?.text.toString() != name) {
                binding.playlistNameTextInputLayout.editText?.setText(name)
            }
        }

        viewModel.playlistDescription.observe(viewLifecycleOwner) { description ->
            if (binding.playlistDescriptionTextInputLayout.editText?.text.toString() != description) {
                binding.playlistDescriptionTextInputLayout.editText?.setText(description)
            }
        }
    }

    private fun savePlaylistChanges() {
        val playlistName = binding.playlistNameTextInputLayout.editText?.text.toString()
        val description = binding.playlistDescriptionTextInputLayout.editText?.text.toString()

        viewModel.updatePlaylist(
            name = playlistName,
            description = description,
            coverUri = coverImageUri
        )

        findNavController().navigateUp()
    }
}
