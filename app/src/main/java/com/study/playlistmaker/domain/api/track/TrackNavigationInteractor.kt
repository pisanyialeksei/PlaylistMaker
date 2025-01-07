package com.study.playlistmaker.domain.api.track

import android.content.Context
import android.content.Intent
import com.study.playlistmaker.domain.models.Track

interface TrackNavigationInteractor {

    fun createTrackIntent(track: Track, context: Context): Intent
    fun getTrackFromIntent(intent: Intent): Track
}
