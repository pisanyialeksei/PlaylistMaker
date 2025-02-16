package com.study.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.ActivityPlayerBinding
import com.study.playlistmaker.dpToPx
import com.study.playlistmaker.formatMsToDuration
import com.study.playlistmaker.player.ui.model.PlayerScreenState
import com.study.playlistmaker.player.ui.navigation.PlayerNavigator
import com.study.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val playerViewModel: PlayerViewModel by viewModel {
        parametersOf(
            PlayerNavigator.getTrackFromIntent(intent)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()

        playerViewModel.screenState.observe(this) {
            render(it)
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.onDestroy()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.playPauseButton.setOnClickListener {
            playerViewModel.togglePlayback()
        }
    }

    private fun render(state: PlayerScreenState) {
        with(binding) {
            name.text = state.track.trackName
            artist.text = state.track.artistName
            currentPosition.text = formatMsToDuration(state.currentPosition.toLong())
            durationValue.text = formatMsToDuration(state.track.trackTimeMillis)

            Glide.with(this@PlayerActivity)
                .load(state.track.getCoverArtwork())
                .placeholder(R.drawable.track_artwork_player_placeholder)
                .transform(CenterCrop(), RoundedCorners(2f.dpToPx(this@PlayerActivity)))
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
