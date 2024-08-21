package com.a2t.myapplication.player.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.FragmentPlayerBinding
import com.a2t.myapplication.player.ui.view_model.PlayerState
import com.a2t.myapplication.player.ui.view_model.PlayerViewModel
import com.a2t.myapplication.search.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

// Для отслеживания внесения изменений в Избранное вводим свойство
var isChangedFavorites: Boolean = false  // По умолчанию - false, с момента нажатия кнопки Избранное и до обработки изменений - true

class PlayerFragment: Fragment() {


    companion object {
        private const val CORNERRADIUS_DP = 8f
        private const val TIME = "time"                     // Тег для сохранения позиции таймера
        private const val EXTRA_TRACK = "EXTRA_TRACK"       // Тег для трека

        fun createArgs(track: Track): Bundle =
            bundleOf(EXTRA_TRACK to track)
    }

    private lateinit var binding: FragmentPlayerBinding
    private var track: Track? = null
    private lateinit var playerState: PlayerState
    private var favoritesButtonState = false
    private lateinit var currentTime: String
    private lateinit var viewModel: PlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = getTrack()          // Получение трека

        val vModel: PlayerViewModel by viewModel { parametersOf(track) }
        viewModel = vModel

        if(savedInstanceState != null) {
            currentTime = savedInstanceState.getString(
                TIME, getString(
                    R.string.start_time))
            binding.tvDuration.text = currentTime
        }

        screenPreparation(track)    // Заполнение экрана

        // Нажатие кнопки Назад закрывает AudioPlayer
        binding.backButton.setOnClickListener {
            //finish()
        }

        // Реакция на нажатие кнопки Play
        binding.playButton.setOnClickListener {
            viewModel.changeStatePlayerAfterClick()
        }

        // Реакция на нажатие кнопки Избранное
        binding.favoritesButton.setOnClickListener {
            track?.let { viewModel.onFavoriteClicked(it) }
            isChangedFavorites = true
        }

        // Получение данных от PlayerViewModel для кнопки Избранное
        viewModel.getStateFavoritesButtonLiveData().observe(viewLifecycleOwner) { newState ->
            favoritesButtonState = newState
            changeIconOfFavoritesButton (favoritesButtonState)
        }

        // Получение данных от PlayerViewModel
        viewModel.getStatePlayerLiveData().observe(viewLifecycleOwner) { newState ->
            playerState = newState
            playbackControl()
        }
    }

    private fun getTrack(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(EXTRA_TRACK, Track::class.java)
        } else requireArguments().getSerializable(EXTRA_TRACK) as Track

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
            .into(binding.ivAlbum)
        // Заполняем поля:
        binding.trackName.text = track?.trackName                        // Назввание трека
        binding.artistName.text = track?.artistName                      // Имя исполнителя
        binding.duration.text = track?.trackTime                         // Продолжительность трека
        if (track?.collectionName?.isNotEmpty() == true) {
            binding.collectionName.text = track.collectionName           // Название альбома
        } else {
            noCollectionName()
        }
        binding.releaseDate.text = track?.releaseDate?.subSequence(0,4)  // Год выхода (первые 4-е символа строки)
        binding.primaryGenreName.text = track?.primaryGenreName          // Жанр трека
        binding.country.text = track?.country                            // Страна исполнителя

        binding.playButton.isEnabled = false                             // При загрузке делаем кнопку Play недоступной до инициализации плейера
        if (track != null) {
            changeIconOfFavoritesButton(track.isFavorite)
        }
    }

    private fun changeIconOfFavoritesButton (favoritesButtonState: Boolean) {
        if (favoritesButtonState) {
            binding.favoritesButton.setImageResource(R.drawable.ic_favorites_red)
        } else {
            binding.favoritesButton.setImageResource(R.drawable.ic_favorites)
        }
    }

    private fun playbackControl() {
        binding.playButton.isEnabled = playerState.isPlayButtonEnabled
        binding.playButton.setImageResource(if(playerState.buttonIcon == "PLAY") R.drawable.ic_play else R.drawable.ic_pause)
        playerState.progress.also { binding.tvDuration.text = it }
    }

    // Если имя альбома пустое
    private fun noCollectionName (){
        binding.collectionName.isVisible = false
        binding.titleCollectionName.isVisible = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TIME, binding.tvDuration.text.toString())
    }

    override fun onPause() {
        super.onPause()
        if (playerState is PlayerState.Playing) {
            viewModel.pause()
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }*/
}