package com.study.playlistmaker.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.study.playlistmaker.R
import com.study.playlistmaker.search.SearchHistoryManager

class TrackAdapter(
    private val trackList: MutableList<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var searchHistoryManager: SearchHistoryManager? = null

    fun setSearchHistoryManager(searchHistoryManager: SearchHistoryManager) {
        this.searchHistoryManager = searchHistoryManager
    }

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
            searchHistoryManager?.let {
                it.addTrackToHistory(item)
            }
        }
    }

    fun updateData(newTrackList: List<Track>) {
        trackList.apply {
            clear()
            addAll(newTrackList)
        }
        notifyDataSetChanged()
    }
}
