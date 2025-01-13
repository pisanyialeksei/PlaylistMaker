package com.study.playlistmaker

import android.content.Context
import android.content.Intent
import com.study.playlistmaker.player.ui.activity.PlayerActivity
import com.study.playlistmaker.search.domain.model.Track

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
