package com.a2t.myapplication

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


private const val CORNERRADIUS_DP = 8f
class AudioPlayerActivity : AppCompatActivity() {
    private var track: Track? = null
    private lateinit var playButton: ImageView
    private lateinit var tvDuration: TextView
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_PROGRESS_DELAY = 300L
    }
    private var playerState = STATE_DEFAULT
    private var url: String? = null
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_TRACK", Track::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_TRACK") as Track
        }

        url = track?.previewUrl                 // Получаем URL трека

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
        tvDuration = findViewById(R.id.tvDuration)                          // Прогресс воспроизведения
        playButton = findViewById(R.id.playButton)                          // Кнопка Play

        // Обновляем прогресс воспроизведения
        runnable = Runnable {
            if (playerState == STATE_PLAYING) {
                tvDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            }
            handler.postDelayed(runnable, REFRESH_PROGRESS_DELAY)
        }
        handler.postDelayed(runnable, REFRESH_PROGRESS_DELAY)

        preparePlayer()     // Подготовка плеера

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

        // Реакция на нажатие кнопки Play
        playButton.setOnClickListener {
            playbackControl()
        }
    }

    // Подготовка плеера
    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_PREPARED
            // Выставляем прогресс воспроизведения 00:00
            tvDuration.text = getString(R.string.start_time)
        }
    }
    // Включение воспроизведения
    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
    }

    // Пауза в воспроизведении
    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
    }

    // Выбор действия при нажатии кнопки Play
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }
}