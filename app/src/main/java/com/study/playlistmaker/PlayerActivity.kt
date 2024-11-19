package com.study.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.study.playlistmaker.track.Track
import com.study.playlistmaker.track.Track.Companion.TRACK_INTENT_KEY

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val currentTrack = Gson().fromJson(intent.getStringExtra(TRACK_INTENT_KEY), Track::class.java)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(currentTrack.getCoverArtwork())
            .placeholder(R.drawable.track_artwork_player_placeholder)
            .transform(CenterCrop())
            .transform(RoundedCorners(2f.dpToPx(this)))
            .into(findViewById<ImageView>(R.id.cover))


        findViewById<TextView>(R.id.name).apply {
            text = currentTrack.trackName
            isSelected = true
        }
        findViewById<TextView>(R.id.artist).apply {
            text = currentTrack.artistName
            isSelected = true
        }
        findViewById<TextView>(R.id.durationValue).text = formatMsToDuration(currentTrack.trackTimeMillis)
        val album = findViewById<TextView>(R.id.albumValue)
        if (currentTrack.collectionName == null) {
            findViewById<TextView>(R.id.albumTitle).isVisible = false
            album.isVisible = false
        } else {
            album.text = currentTrack.collectionName
        }
        findViewById<TextView>(R.id.yearValue).text = currentTrack.releaseDate.substringBefore('-')
        findViewById<TextView>(R.id.genreValue).text = currentTrack.primaryGenreName
        findViewById<TextView>(R.id.countryValue).text = currentTrack.country
    }
}
