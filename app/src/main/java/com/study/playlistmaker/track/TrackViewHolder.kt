package com.study.playlistmaker.track

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.R
import com.study.playlistmaker.dpToPx
import com.study.playlistmaker.formatMsToDuration


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val itemArtwork: ImageView = itemView.findViewById(R.id.search_item_artwork)
    private val itemName: TextView = itemView.findViewById(R.id.search_item_name)
    private val itemArtist: TextView = itemView.findViewById(R.id.search_item_artist)
    private val itemDuration: TextView = itemView.findViewById(R.id.search_item_duration)

    fun bind(model: Track) {
        val context = itemView.context
        Glide.with(context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_artwork_list_placeholder)
            .transform(CenterCrop())
            .transform(RoundedCorners(2f.dpToPx(context)))
            .into(itemArtwork)

        itemName.text = model.trackName
        itemArtist.text = model.artistName
        itemDuration.text = formatMsToDuration(model.trackTimeMillis)

        itemArtist.requestLayout()
    }
}
