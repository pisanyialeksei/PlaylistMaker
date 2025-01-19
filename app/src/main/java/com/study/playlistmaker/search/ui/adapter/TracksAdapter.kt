package com.study.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.TracksItemBinding
import com.study.playlistmaker.dpToPx
import com.study.playlistmaker.formatMsToDuration
import com.study.playlistmaker.search.domain.model.Track

class TracksAdapter(
    private val trackList: MutableList<Track>,
    private val clickListener: (Track) -> Unit,
) : RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TracksViewHolder(TracksItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val item = trackList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener.invoke(item)
        }
    }

    fun updateData(newTrackList: List<Track>) {
        trackList.clear()
        trackList.addAll(newTrackList)
        notifyDataSetChanged()
    }

    class TracksViewHolder(private val binding: TracksItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Track) {
            val context = itemView.context
            Glide.with(context)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.track_artwork_list_placeholder)
                .transform(CenterCrop())
                .transform(RoundedCorners(2f.dpToPx(context)))
                .into(binding.tracksItemArtwork)

            binding.tracksItemName.text = model.trackName
            binding.tracksItemArtist.text = model.artistName
            binding.tracksItemDuration.text = formatMsToDuration(model.trackTimeMillis)

            binding.tracksItemArtist.requestLayout()
        }
    }
}
