package com.study.playlistmaker.player.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.PlaylistsBottomSheetItemBinding
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.utils.dpToPx

class PlaylistsBottomSheetAdapter(
    private var playlists: List<Playlist>,
    private val onPlaylistClickListener: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsBottomSheetAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistsBottomSheetItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(
        private val binding: PlaylistsBottomSheetItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Playlist) {
            val context = itemView.context
            Glide.with(context)
                .load(model.cover)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.track_artwork_player_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(2f.dpToPx(itemView.context))
                )
                .into(binding.bottomSheetPlaylistItemCover)

            binding.bottomSheetPlaylistItemName.text = model.name
            binding.bottomSheetPlaylistItemCounter.text = context.resources.getQuantityString(
                R.plurals.playlist_tracks_count,
                model.tracksCount,
                model.tracksCount
            )

            itemView.setOnClickListener {
                onPlaylistClickListener(model)
            }
        }
    }
}
