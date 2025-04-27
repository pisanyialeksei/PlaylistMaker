package com.study.playlistmaker.library.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.PlaylistsItemBinding
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.utils.dpToPx

class PlaylistsAdapter(
    private val playlists: MutableList<Playlist>,
    private val clickListener: (Playlist) -> Unit,
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistsViewHolder(PlaylistsItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun updateData(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    inner class PlaylistsViewHolder(private val binding: PlaylistsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Playlist) {
            val context = itemView.context
            Glide.with(context)
                .load(model.cover)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.track_artwork_player_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(8f.dpToPx(context))
                )
                .into(binding.playlistItemCover)

            binding.playlistItemName.text = model.name
            binding.playlistItemCounter.text = context.resources.getQuantityString(
                R.plurals.playlist_tracks_count,
                model.tracksCount,
                model.tracksCount
            )

            itemView.setOnClickListener {
                clickListener.invoke(model)
            }
        }
    }
}
