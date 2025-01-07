package com.study.playlistmaker.domain.api.track

import android.content.Context
import android.content.Intent
import com.study.playlistmaker.data.dto.TrackDto

interface TrackNavigationRepository {

    fun createTrackIntent(trackDto: TrackDto, context: Context): Intent
    fun getTrackFromIntent(intent: Intent): TrackDto
}
