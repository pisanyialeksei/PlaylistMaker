package com.study.playlistmaker.player.ui.navigation

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.study.playlistmaker.player.ui.activity.PlayerActivity
import com.study.playlistmaker.player.ui.model.PlayerTrack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PlayerNavigator : KoinComponent {

    private const val TRACK_EXTRA = "track"

    private val gson: Gson by inject()

    fun createPlayerIntent(track: PlayerTrack, context: Context): Intent {
        val trackJson = gson.toJson(track)
        return Intent(context, PlayerActivity::class.java).apply {
            putExtra(TRACK_EXTRA, trackJson)
        }
    }

    fun getTrackFromIntent(intent: Intent): PlayerTrack {
        val trackJson = intent.getStringExtra(TRACK_EXTRA)
        return gson.fromJson(trackJson, PlayerTrack::class.java)
    }
}
