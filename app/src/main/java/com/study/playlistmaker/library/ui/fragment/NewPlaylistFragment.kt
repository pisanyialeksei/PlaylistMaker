package com.study.playlistmaker.library.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.study.playlistmaker.library.ui.view_model.NewPlaylistViewModel
import com.study.playlistmaker.sharing.data.StringProvider
import com.study.playlistmaker.utils.dpToPx
import com.study.playlistmaker.utils.showToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NewPlaylistFragment : Fragment() {

    lateinit var binding: FragmentNewPlaylistBinding

    open val viewModel: NewPlaylistViewModel by viewModel()
    private val stringProvider: StringProvider by inject()

    var coverImageUri: Uri? = null

    private val visualMediaPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            coverImageUri = uri
            Glide.with(requireContext())
                .load(uri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(8f.dpToPx(requireContext()))
                )
                .into(binding.playlistCoverImageView)
        } else {
            showToast(requireContext(), stringProvider.getString(R.string.no_image_toast))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            }
        )

        viewModel.isButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.newPlaylistCreateButton.isEnabled = isEnabled
        }

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            handleBackNavigation()
        }

        binding.playlistNameTextInputLayout.editText?.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                viewModel.onNameInputTextChanged(text.toString())
            }
        )

        binding.playlistCoverImageView.setOnClickListener {
            visualMediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.newPlaylistCreateButton.setOnClickListener {
            val playlistName = binding.playlistNameTextInputLayout.editText?.text.toString()

            viewModel.createPlaylist(
                name = playlistName,
                description = binding.playlistDescriptionTextInputLayout.editText?.text.toString(),
                coverUri = coverImageUri,
            )

            findNavController().navigateUp()
            showToast(requireContext(), stringProvider.getString(R.string.playlist_created, playlistName))
        }
    }

    private fun handleBackNavigation() {
        if (coverImageUri != null
            || binding.playlistNameTextInputLayout.editText?.text.toString().isNotEmpty()
            || binding.playlistDescriptionTextInputLayout.editText?.text.toString().isNotEmpty()
        ) {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialog)
                .setTitle(stringProvider.getString(R.string.new_playlist_cancellation_title))
                .setMessage(stringProvider.getString(R.string.new_playlist_cancellation_message))
                .setNegativeButton(stringProvider.getString(R.string.new_playlist_cancellation_negative_button)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(stringProvider.getString(R.string.new_playlist_cancellation_positive_button)) { _, _ ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }
}
