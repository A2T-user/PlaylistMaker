package com.a2t.myapplication.ui.audioplayer

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.a2t.myapplication.Creator
import com.a2t.myapplication.R
import com.a2t.myapplication.domain.api.PlayerInteractor
import com.a2t.myapplication.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

private const val CORNERRADIUS_DP = 8f
class AudioPlayerActivity : AppCompatActivity() {
    private var track: Track? = null
    private lateinit var playButton: ImageView
    private lateinit var ivAlbum: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var collectionName: TextView
    private lateinit var titleCollectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
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
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var interactor: PlayerInteractor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val backButton = findViewById<ImageView>(R.id.backButton)           // Кнопка Назад
        ivAlbum = findViewById(R.id.ivAlbum)                     // Обложка альбома
        trackName = findViewById(R.id.trackName)              // Назввание трека
        artistName = findViewById(R.id.artistName)            // Имя исполнителя
        duration = findViewById(R.id.duration)                // Продолжительность трека
        collectionName = findViewById(R.id.collectionName)    // Название альбома
        titleCollectionName = findViewById(R.id.title_collectionName)
        releaseDate = findViewById(R.id.releaseDate)          // Год выхода
        primaryGenreName = findViewById(R.id.primaryGenreName)// Жанр трека
        country = findViewById(R.id.country)                  // Страна исполнителя
        tvDuration = findViewById(R.id.tvDuration)                          // Прогресс воспроизведения
        playButton = findViewById(R.id.playButton)                          // Кнопка Play

        // Получение трека
        track = getTrack()
        // Получение URL трека
        url = track?.previewUrl

        interactor = Creator.provideMoviesInteractor()

        // Заполнение экрана
        screenPreparation(track)


        // Обновляем прогресс воспроизведения
        runnable = Runnable {
            if (playerState == STATE_PLAYING) {
                tvDuration.text = interactor.currentPosition()
            }
            handler.postDelayed(runnable, REFRESH_PROGRESS_DELAY)
        }
        handler.postDelayed(runnable, REFRESH_PROGRESS_DELAY)

        preparePlayer()     // Подготовка плеера

        // Нажатие кнопки Назад закрывает AudioPlayer
        backButton.setOnClickListener {
            finish()
        }

        // Реакция на нажатие кнопки Play
        playButton.setOnClickListener {
            playbackControl()
        }
    }

    // Подготовка плеера
    private fun preparePlayer() {
        interactor.setDataSource(url)
        interactor.preparePlayer()
        interactor.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        interactor.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_PREPARED
            // Выставляем прогресс воспроизведения 00:00
            tvDuration.text = getString(R.string.start_time)
        }
    }
    // Включение воспроизведения
    private fun startPlayer() {
        interactor.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
    }

    // Пауза в воспроизведении
    private fun pausePlayer() {
        interactor.pause()
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
        interactor.release()
        handler.removeCallbacks(runnable)
    }

    private fun getTrack(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_TRACK", Track::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_TRACK") as Track
        }

    }


    private fun screenPreparation(track: Track?) {
        // Выводим обложку альбома
        Glide.with(this)
            .load(track?.artworkUrl512)
            .placeholder(R.drawable.ic_album_big)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        CORNERRADIUS_DP,
                        this.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(ivAlbum)
        // Заполняем поля:
        trackName.text = track?.trackName                        // Назввание трека
        artistName.text = track?.artistName                      // Имя исполнителя
        // Продолжительность трека
        duration.text = track?.trackTime
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