package com.a2t.myapplication.player.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.R
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist

class SmallPlaylistAdapter(private val clickListener: SmallPlaylistClickListener): RecyclerView.Adapter<SmallPlaylistViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.small_playlist_item, parent, false)
        return SmallPlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: SmallPlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onSmallPlaylistClick(playlist) }
    }

    fun interface SmallPlaylistClickListener {
        fun onSmallPlaylistClick(playlist: Playlist)
    }
}