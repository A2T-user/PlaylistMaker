package com.a2t.myapplication.player.ui.fragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.R
import com.a2t.myapplication.util.DifferentStrings
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.bumptech.glide.Glide

class SmallPlaylistViewHolder (view: View): RecyclerView.ViewHolder(view) {
    private var playlistId: Long? = null
    private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
    private val tvName: TextView = itemView.findViewById(R.id.tvName)
    private val tvCountTracks: TextView = itemView.findViewById(R.id.tvCountTracks)

    fun bind(playlist: Playlist) {
        playlistId = playlist.playlistId
        // Заполнение иконки Альбом
        Glide.with(itemView)
            .load(playlist.playlistUri)
            .placeholder(R.drawable.ic_album_big)
            .centerCrop()
            .into(ivCover)
        playlistId = playlist.playlistId
        tvName.text = playlist.playlistName

        tvCountTracks.text = DifferentStrings.countTracks(playlist.playlistIdList.size)
    }
}