package com.study.playlistmaker.domain.impl

import android.content.Context
import android.content.Intent
import com.study.playlistmaker.data.toDto
import com.study.playlistmaker.data.toModel
import com.study.playlistmaker.domain.api.track.TrackNavigationInteractor
import com.study.playlistmaker.domain.api.track.TrackNavigationRepository
import com.study.playlistmaker.domain.models.Track

class TrackNavigationInteractorImpl(private val repository: TrackNavigationRepository) : TrackNavigationInteractor {

    override fun createTrackIntent(track: Track, context: Context): Intent {
        val trackDto = track.toDto()
        return repository.createTrackIntent(trackDto, context)
    }

    override fun getTrackFromIntent(intent: Intent): Track {
        val trackDto = repository.getTrackFromIntent(intent)
        return trackDto.toModel()
    }
}
