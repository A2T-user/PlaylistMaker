package com.a2t.myapplication.mediateca.ui.fragments.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.R
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist


class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }
}