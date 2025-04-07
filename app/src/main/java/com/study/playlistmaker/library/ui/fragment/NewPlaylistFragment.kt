package com.study.playlistmaker.library.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.study.playlistmaker.library.ui.view_model.NewPlaylistViewModel
import com.study.playlistmaker.sharing.data.StringProvider
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentNewPlaylistBinding

    private val viewModel: NewPlaylistViewModel by viewModel()
    private val stringProvider: StringProvider by inject()

    private var coverImageUri: Uri? = null

    private val visualMediaPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            coverImageUri = uri
            binding.playlistCoverImageView.setImageURI(uri)
        } else {
            Toast.makeText(context, stringProvider.getString(R.string.no_image_toast), Toast.LENGTH_SHORT).show()
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
            val coverPath = coverImageUri?.let {
                val file = getCoverFile()
                saveImageToPrivateStorage(coverImageUri!!, file)
                file.absolutePath
            }

            viewModel.createPlaylist(
                name = playlistName,
                description = binding.playlistDescriptionTextInputLayout.editText?.text.toString(),
                coverPath = coverPath
            )

            findNavController().navigateUp()
            Toast.makeText(requireContext(), requireContext().getString(R.string.playlist_created, playlistName), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCoverFile(): File {
        val playlistName = binding.playlistNameTextInputLayout.editText?.text.toString()
        val fileName = "playlist_${playlistName.replace(" ", "_").lowercase()}.jpg"
        val directoryPath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")

        if (!directoryPath.exists()) {
            directoryPath.mkdirs()
        }

        return File(directoryPath, fileName)
    }

    private fun saveImageToPrivateStorage(uri: Uri, outputFile: File) {
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(outputFile)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
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
