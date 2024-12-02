package com.study.playlistmaker.track

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.study.playlistmaker.PlayerActivity
import com.study.playlistmaker.R
import com.study.playlistmaker.search.SearchHistoryManager
import com.study.playlistmaker.track.Track.Companion.TRACK_INTENT_KEY

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val searchHistoryManager: SearchHistoryManager,
    private val context: Context,
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = trackList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                searchHistoryManager.addTrackToHistory(item)
                val itemJson = Gson().toJson(item)
                context.startActivity(
                    Intent(context, PlayerActivity::class.java).putExtra(TRACK_INTENT_KEY, itemJson)
                )
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
    }
}
