package com.a2t.myapplication.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.a2t.myapplication.R
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.player.ui.view_model.PlayerData
import com.a2t.myapplication.player.ui.view_model.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

private const val CORNERRADIUS_DP = 8f

class PlayerActivity : AppCompatActivity() {
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
        private const val REFRESH_PROGRESS_DELAY = 300L
    }
    private var playerState = PlayerData.STATE_DEFAULT
    private var url: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        viewModel = ViewModelProvider(this, PlayerViewModel.getViewModelFactory())[PlayerViewModel::class.java]

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

        track = getTrack()          // Получение трека

        url = track?.previewUrl     // Получение URL трека

        preparePlayer()             // Подготовка плеера

        screenPreparation(track)    // Заполнение экрана

        // Обновляем прогресс воспроизведения
        runnable = Runnable {
            if (playerState == PlayerData.STATE_PLAYING) {
                tvDuration.text = viewModel.getCurrentPosition()
            }
            handler.postDelayed(runnable, REFRESH_PROGRESS_DELAY)
        }
        handler.postDelayed(runnable, REFRESH_PROGRESS_DELAY)

        // Нажатие кнопки Назад закрывает AudioPlayer
        backButton.setOnClickListener {
            finish()
        }

        // Реакция на нажатие кнопки Play
        playButton.setOnClickListener {
            viewModel.changeStatePlayerAfterClick()
            //playbackControl()
        }

        viewModel.getStatePlayerLiveData().observe(this) { newState ->
            playerState = newState
            playbackControl()
        }
    }

    // Подготовка плеера
    private fun preparePlayer() {
        viewModel.setDataSource(url)
        viewModel.preparePlayer()
        viewModel.setOnPreparedListener {
            viewModel.updateStatePlayerLiveData(PlayerData.STATE_PREPARED)
        }
        viewModel.setOnCompletionListener {
            viewModel.updateStatePlayerLiveData(PlayerData.STATE_PREPARED)
        }
    }

    // После подготовки плеера
    private fun afterPreparingPlayer () {
        playButton.isEnabled = true
        playButton.setImageResource(R.drawable.ic_play)
        // Выставляем прогресс воспроизведения 00:00
        tvDuration.text = getString(R.string.start_time)
    }

    // Во время подготовки плеера
    private fun duringPreparation () {
        playButton.isEnabled = false
    }

    // Включение воспроизведения
    private fun startPlayer() {
        viewModel.start()
        playButton.setImageResource(R.drawable.ic_pause)
        viewModel.updateStatePlayerLiveData(PlayerData.STATE_PLAYING)
    }
    // Пауза в воспроизведении
    private fun pausePlayer() {
        viewModel.pause()
        playButton.setImageResource(R.drawable.ic_play)
        viewModel.updateStatePlayerLiveData(PlayerData.STATE_PAUSED)
    }

    // Выбор действия при нажатии кнопки Play
    private fun playbackControl() {
        when(playerState) {
            PlayerData.STATE_PLAYING -> startPlayer()
            PlayerData.STATE_PAUSED -> pausePlayer()
            PlayerData.STATE_PREPARED -> afterPreparingPlayer()
            PlayerData.STATE_DEFAULT -> duringPreparation()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
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
        duration.text = track?.trackTime                         // Продолжительность трека
        collectionName.text = track?.collectionName              // Название альбома
        // если название альбома нет, то эту информацию на экране не показываем
        val visible = track?.collectionName?.isNotEmpty()
        if (visible != null) {
            collectionName.isVisible = visible
            titleCollectionName.isVisible = visible
        }

        releaseDate.text = track?.releaseDate?.subSequence(0,4)  // Год выхода (первые 4-е символа строки)
        primaryGenreName.text = track?.primaryGenreName          // Жанр трека
        country.text = track?.country                            // Страна исполнителя
    }
}