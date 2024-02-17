package com.a2t.myapplication

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)

    fun bind(item: Track) {
        // Заполнение иконки Альбом
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_album)
            .centerCrop()
            .transform(RoundedCorners(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, itemView.resources.displayMetrics).toInt()))
            .into(ivArtwork)

        // Заполнение поля Название композиции
        tvTrackName.text = item.trackName

        // Заполнение поля Имя исполнителя
        "${item.artistName}  \u2022  ${item.trackTime}".also { tvArtistName.text = it }
    }
}