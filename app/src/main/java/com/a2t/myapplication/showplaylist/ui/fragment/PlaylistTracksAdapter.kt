package com.a2t.myapplication.showplaylist.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.R
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.fragment.TrackViewHolder

class PlaylistTracksAdapter(
    private val clickListener: TrackClickListener,
    private val longClickListener: TrackLongClickListener,
    ) : RecyclerView.Adapter<TrackViewHolder> () {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { clickListener.onTrackClick(track) }
        holder.itemView.setOnLongClickListener{ longClickListener.onTrackLongClick(track) }
    }

    override fun getItemCount() = tracks.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface TrackLongClickListener {
        fun onTrackLongClick(track: Track): Boolean
    }

}