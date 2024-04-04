package com.a2t.myapplication

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    private var trackId: Int? = null
    val holderContainer: LinearLayout = itemView.findViewById(R.id.holderContainer)
    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    fun bind(item: Track) {
        trackId = item.trackId
        // Заполнение иконки Альбом
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_album)
            .centerCrop()
            .transform(RoundedCorners(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNERRADIUS_DP, itemView.resources.displayMetrics).toInt()))
            .into(ivArtwork)

        // Заполнение полей Название композиции, Имя исполнителя, Продолжительность трека
        tvTrackName.text = item.trackName
        tvArtistName.text = item.artistName
        tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
    }
    fun getTrackID (): Int? {
        return trackId
    }
}

private const val CORNERRADIUS_DP = 2f