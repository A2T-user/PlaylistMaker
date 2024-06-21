package com.a2t.myapplication.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.a2t.myapplication.R
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.player.ui.view_model.PlayerState
import com.a2t.myapplication.player.ui.view_model.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val CORNERRADIUS_DP = 8f
private const val TIME = "time"                     // Тег для сохранения позиции таймера

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
    private var playerState = PlayerState.STATE_DEFAULT
    private lateinit var currentTime: String
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var viewModel: PlayerViewModel

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

        track = getTrack()          // Получение трека

        val vModel: PlayerViewModel by viewModel { parametersOf(track) }
        viewModel = vModel

        currentTime = getString(R.string.start_time)
        if(savedInstanceState != null) {
            currentTime = savedInstanceState.getString(TIME, getString(R.string.start_time))
            tvDuration.text = currentTime
        }

        screenPreparation(track)    // Заполнение экрана

        // Обновляем прогресс воспроизведения
        runnable = Runnable {
            if (playerState == PlayerState.STATE_PLAYING) {
                tvDuration.text = viewModel.currentPosition()
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
        }

        // Получение данных от PlayerViewModel
        viewModel.getStatePlayerLiveData().observe(this) { newState ->
            playerState = newState
            playbackControl()
        }
    }

    // После подготовки плеера
    private fun afterPreparingPlayer () {
        playButton.isEnabled = true
        playButton.setImageResource(R.drawable.ic_play)
        // Выставляем прогресс воспроизведения 00:00
        currentTime = getString(R.string.start_time)
        tvDuration.text = currentTime
    }

    // Во время подготовки плеера
    private fun duringPreparation () {
        playButton.isEnabled = false
    }

    // Включение воспроизведения
    private fun startPlayer() {
        viewModel.start()
        playButton.setImageResource(R.drawable.ic_pause)
        viewModel.updateStatePlayerLiveData(PlayerState.STATE_PLAYING)
    }
    // Пауза в воспроизведении
    private fun pausePlayer() {
        viewModel.pause()
        playButton.setImageResource(R.drawable.ic_play)
        viewModel.updateStatePlayerLiveData(PlayerState.STATE_PAUSED)
    }

    // Выбор действия при нажатии кнопки Play
    private fun playbackControl() {
        when(playerState) {
            PlayerState.STATE_ERROR -> showError()
            PlayerState.STATE_NO_ALBUM_NAME -> noCollectionName()
            PlayerState.STATE_PLAYING -> startPlayer()
            PlayerState.STATE_PAUSED -> pausePlayer()
            PlayerState.STATE_PREPARED -> afterPreparingPlayer()
            PlayerState.STATE_DEFAULT -> duringPreparation()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
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
        releaseDate.text = track?.releaseDate?.subSequence(0,4)  // Год выхода (первые 4-е символа строки)
        primaryGenreName.text = track?.primaryGenreName          // Жанр трека
        country.text = track?.country                            // Страна исполнителя
    }

    // Если имя альбома пустое
    private fun noCollectionName (){
        collectionName.isVisible = false
        titleCollectionName.isVisible = false

    }

    // Состояние ошибки, если передан track == null (возможность чисто теоретическая)
    private fun showError () {
        Toast.makeText(this@PlayerActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TIME, tvDuration.text.toString())
    }
}