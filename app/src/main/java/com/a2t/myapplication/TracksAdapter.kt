package com.a2t.myapplication


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter (myContext: Context) : RecyclerView.Adapter<TrackViewHolder> () {
    var tracks = ArrayList<Track>()
    private val context = myContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.holderContainer.setOnClickListener {
            val searchHistory = SearchHistory()
            val searchHistoryList = searchHistory.readSearchHistory()
            searchHistory.addTrackToSearchHistory(searchHistoryList, track)
            if (screenMode == FilterScreenMode.HISTORY) {
                tracks.clear()
                tracks.addAll(searchHistoryList)
                notifyItemMoved(position, 0)
                notifyItemRangeChanged(0, position + 1)
            }
            // Открыть AudioPlayer
            val intent = Intent(context, AudioPlayerActivity::class.java)
            intent.putExtra("EXTRA_TRACK", track)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = tracks.size
}