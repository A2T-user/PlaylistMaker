package com.a2t.myapplication


import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter (myContext: Context) : RecyclerView.Adapter<TrackViewHolder> () {
    var tracks = ArrayList<Track>()
    private val context = myContext
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.holderContainer.setOnClickListener {
            if (clickDebounce()) {
                val searchHistory = SearchHistory()
                val searchHistoryList = searchHistory.readSearchHistory()
                searchHistory.addTrackToSearchHistory(
                    searchHistoryList,
                    track
                )     // Добавляем трек в историю поиска
                if (screenMode == FilterScreenMode.HISTORY) {                       // Если открыта история поиска,
                    tracks.clear()                                                  // Меняем положение трека на экране
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
    }

    override fun getItemCount() = tracks.size

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}