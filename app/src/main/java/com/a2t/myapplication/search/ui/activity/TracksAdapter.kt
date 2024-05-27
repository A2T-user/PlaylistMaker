package com.a2t.myapplication.search.ui.activity


import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.R
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.player.ui.activity.PlayerActivity
import com.a2t.myapplication.search.ui.models.FilterScreenMode
import com.a2t.myapplication.search.ui.view_model.SearchViewModel

class TracksAdapter (myContext: Context, searchViewModel: SearchViewModel) : RecyclerView.Adapter<TrackViewHolder> () {
    var tracks = ArrayList<Track>()
    private val context = myContext
    private val viewModel = searchViewModel
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
                viewModel.addTrackToSearchHistory(tracks, track)                    // Добавляем трек в историю поиска
                if (screenMode == FilterScreenMode.HISTORY) {                       // Если открыта история поиска, обновляем содержимое рециклера
                    viewModel.processingSearchHistory()
                }
                // Открыть AudioPlayer
                val intent = Intent(context, PlayerActivity::class.java)
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