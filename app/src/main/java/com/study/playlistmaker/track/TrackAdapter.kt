package com.study.playlistmaker.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.study.playlistmaker.R

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val clickListener: (Track) -> Unit,
) : RecyclerView.Adapter<TrackViewHolder>() {

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
            clickListener.invoke(item)
        }
    }
}
