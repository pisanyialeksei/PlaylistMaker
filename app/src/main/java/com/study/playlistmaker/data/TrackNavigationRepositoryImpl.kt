package com.study.playlistmaker.data

import android.content.Context
import android.content.Intent
import com.study.playlistmaker.data.dto.TrackDto
import com.study.playlistmaker.domain.api.track.TrackNavigationRepository
import com.study.playlistmaker.gson
import com.study.playlistmaker.ui.PlayerActivity

class TrackNavigationRepositoryImpl : TrackNavigationRepository {

    override fun createTrackIntent(trackDto: TrackDto, context: Context): Intent {
        val trackJson = gson.toJson(trackDto)
        return Intent(context, PlayerActivity::class.java).apply {
            putExtra(TRACK_EXTRA, trackJson)
        }
    }

    override fun getTrackFromIntent(intent: Intent): TrackDto {
        val trackJson = intent.getStringExtra(TRACK_EXTRA)
        return gson.fromJson(trackJson, TrackDto::class.java)
    }

    companion object {
        const val TRACK_EXTRA = "track"
    }
}
