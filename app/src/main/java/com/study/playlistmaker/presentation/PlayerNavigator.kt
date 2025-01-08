package com.study.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import com.study.playlistmaker.domain.models.Track
import com.study.playlistmaker.gson
import com.study.playlistmaker.ui.PlayerActivity

object PlayerNavigator {

    private const val TRACK_EXTRA = "track"

    fun createPlayerIntent(track: Track, context: Context): Intent {
        val trackJson = gson.toJson(track)
        return Intent(context, PlayerActivity::class.java).apply {
            putExtra(TRACK_EXTRA, trackJson)
        }
    }

    fun getTrackFromIntent(intent: Intent): Track {
        val trackJson = intent.getStringExtra(TRACK_EXTRA)
        return gson.fromJson(trackJson, Track::class.java)
    }
}
