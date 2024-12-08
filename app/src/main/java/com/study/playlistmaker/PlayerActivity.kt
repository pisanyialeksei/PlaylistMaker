package com.study.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.track.Track
import com.study.playlistmaker.track.Track.Companion.TRACK_INTENT_KEY

class PlayerActivity : AppCompatActivity() {

    private lateinit var playPauseButton: ImageButton
    private lateinit var previewUrl: String
    private lateinit var currentPosition: TextView

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val currentPositionUpdateRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                currentPosition.text = formatMsToDuration(mediaPlayer.currentPosition.toLong())
                mainThreadHandler.postDelayed(this, CURRENT_POSITION_UPDATE_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val currentTrack = gson.fromJson(intent.getStringExtra(TRACK_INTENT_KEY), Track::class.java)
        previewUrl = currentTrack.previewUrl

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(currentTrack.getCoverArtwork())
            .placeholder(R.drawable.track_artwork_player_placeholder)
            .transform(CenterCrop())
            .transform(RoundedCorners(2f.dpToPx(this)))
            .into(findViewById(R.id.cover))


        findViewById<TextView>(R.id.name).apply {
            text = currentTrack.trackName
            isSelected = true
        }
        findViewById<TextView>(R.id.artist).apply {
            text = currentTrack.artistName
            isSelected = true
        }

        currentPosition = findViewById(R.id.current_duration)
        currentPosition.text = formatMsToDuration(0L)

        playPauseButton = findViewById(R.id.play_pause_button)
        playPauseButton.setOnClickListener {
            setPlaybackControl()
        }

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

        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playPauseButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playPauseButton.setBackgroundResource(R.drawable.play_button_background)
            mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
            currentPosition.text = formatMsToDuration(0L)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playPauseButton.setBackgroundResource(R.drawable.pause_button_background)
        mainThreadHandler.post(currentPositionUpdateRunnable)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playPauseButton.setBackgroundResource(R.drawable.play_button_background)
        mainThreadHandler.removeCallbacks(currentPositionUpdateRunnable)
        playerState = STATE_PAUSED
    }

    private fun setPlaybackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val CURRENT_POSITION_UPDATE_DELAY = 500L
    }
}
