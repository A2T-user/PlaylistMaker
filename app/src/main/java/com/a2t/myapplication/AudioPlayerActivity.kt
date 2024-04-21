package com.a2t.myapplication

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


private const val CORNERRADIUS_DP = 8f
class AudioPlayerActivity : AppCompatActivity() {
    private var track: Track? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_TRACK", Track::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_TRACK") as Track
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)         // Кнопка Назад
        val ivAlbum = findViewById<ImageView>(R.id.ivAlbum)                 // Обложка альбома
        val trackName = findViewById<TextView>(R.id.trackName)              // Назввание трека
        val artistName = findViewById<TextView>(R.id.artistName)            // Имя исполнителя
        val duration = findViewById<TextView>(R.id.duration)                // Продолжительность трека
        val collectionName = findViewById<TextView>(R.id.collectionName)    // Название альбома
        val titleCollectionName = findViewById<TextView>(R.id.title_collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)          // Год выхода
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)// Жанр трека
        val country = findViewById<TextView>(R.id.country)                  // Страна исполнителя


        // Нажатие кнопки Назад закрывает AudioPlayer
        backButton.setOnClickListener {
            finish()
        }

        // Выводим обложку альбома
        Glide.with(this)
            .load(track?.getArtworkUrl512())
            .placeholder(R.drawable.ic_album_big)
            .centerCrop()
            .transform(RoundedCorners(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNERRADIUS_DP, this.resources.displayMetrics).toInt()))
            .into(ivAlbum)
        // Заполняем поля:
        trackName.text = track?.trackName                        // Назввание трека
        artistName.text = track?.artistName                      // Имя исполнителя
        // Продолжительность трека
        duration.text = track?.trackDurationInString()
        // Название альбома
        collectionName.text = track?.collectionName
        // если название альбома нет, то эту информацию на экране не показываем
        val visible = track?.collectionName?.isNotEmpty()
        if (visible != null) {
            collectionName.isVisible = visible
            titleCollectionName.isVisible = visible
        }

        releaseDate.text = track?.releaseDate?.subSequence(0,4)   // Год выхода (первые 4-е символа строки)
        primaryGenreName.text = track?.primaryGenreName          // Жанр трека
        country.text = track?.country                            // Страна исполнителя
    }
}