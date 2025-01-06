package com.study.playlistmaker.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.Creator
import com.study.playlistmaker.R
import com.study.playlistmaker.domain.api.mediaplayer.MediaPlayerInteractor
import com.study.playlistmaker.domain.models.Track
import com.study.playlistmaker.domain.models.Track.Companion.TRACK_INTENT_KEY
import com.study.playlistmaker.dpToPx
import com.study.playlistmaker.formatMsToDuration
import com.study.playlistmaker.gson

class PlayerActivity : AppCompatActivity() {

    private lateinit var currentTrack: Track

    private lateinit var toolbar: Toolbar
    private lateinit var cover: ImageView
    private lateinit var playPauseButton: ImageButton
    private lateinit var currentPosition: TextView

    private lateinit var mediaPlayerInteractor: MediaPlayerInteractor

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val currentPositionUpdateRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayerInteractor.isPlaying()) {
                currentPosition.text = formatMsToDuration(mediaPlayerInteractor.getCurrentPosition().toLong())
                mainThreadHandler.postDelayed(this, CURRENT_POSITION_UPDATE_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentTrack = gson.fromJson(intent.getStringExtra(TRACK_INTENT_KEY), Track::class.java)
        mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()

        initializeViews()
        setupListeners()

        Glide.with(this)
            .load(currentTrack.getCoverArtwork())
            .placeholder(R.drawable.track_artwork_player_placeholder)
            .transform(CenterCrop())
            .transform(RoundedCorners(2f.dpToPx(this)))
            .into(cover)

        mediaPlayerInteractor.prepare(
            url = currentTrack.previewUrl,
            onPrepared = {
                playPauseButton.isEnabled = true
            },
            onCompletion = {
                playPauseButton.setBackgroundResource(R.drawable.play_button_background)
                mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
                currentPosition.text = formatMsToDuration(0L)
            }
        )
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerInteractor.pause()
        updatePlaybackUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerInteractor.release()
        mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        cover = findViewById(R.id.cover)
        findViewById<TextView>(R.id.name).apply {
            text = currentTrack.trackName
            isSelected = true
        }
        findViewById<TextView>(R.id.artist).apply {
            text = currentTrack.artistName
            isSelected = true
        }
        currentPosition = findViewById<TextView?>(R.id.current_duration).apply {
            text = formatMsToDuration(0L)
        }
        playPauseButton = findViewById(R.id.play_pause_button)
        findViewById<TextView>(R.id.duration_value).text = formatMsToDuration(currentTrack.trackTimeMillis)
        val album = findViewById<TextView>(R.id.album_value)
        if (currentTrack.collectionName == null) {
            findViewById<TextView>(R.id.album_title).isVisible = false
            album.isVisible = false
        } else {
            album.text = currentTrack.collectionName
        }
        findViewById<TextView>(R.id.year_value).text = currentTrack.releaseDate.substringBefore('-')
        findViewById<TextView>(R.id.genre_value).text = currentTrack.primaryGenreName
        findViewById<TextView>(R.id.country_value).text = currentTrack.country
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        playPauseButton.setOnClickListener {
            mediaPlayerInteractor.setPlaybackControl()
            updatePlaybackUI()
        }
    }

    private fun updatePlaybackUI() {
        if (mediaPlayerInteractor.isPlaying()) {
            playPauseButton.setBackgroundResource(R.drawable.pause_button_background)
            mainThreadHandler.post(currentPositionUpdateRunnable)
        } else {
            playPauseButton.setBackgroundResource(R.drawable.play_button_background)
            mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
        }
    }

    companion object {
        private const val CURRENT_POSITION_UPDATE_DELAY = 500L
    }
}
