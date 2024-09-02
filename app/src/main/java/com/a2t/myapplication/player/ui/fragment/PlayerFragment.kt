package com.a2t.myapplication.player.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.FragmentPlayerBinding
import com.a2t.myapplication.mediateca.ui.view_model.PlaylistViewModel
import com.a2t.myapplication.player.ui.view_model.PlayerState
import com.a2t.myapplication.player.ui.view_model.PlayerViewModel
import com.a2t.myapplication.root.ui.activity.RootActivity
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

// Для отслеживания внесения изменений в Избранное вводим свойство
var isChangedFavorites: Boolean = false  // По умолчанию - false, с момента нажатия кнопки Избранное и до обработки изменений - true

private const val CLICK_DEBOUNCE_DELAY = 1000L

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
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var  adapter: SmallPlaylistAdapter
    private val playlists = arrayListOf<Playlist>()
    private var isClickAllowed = true

    private val playlistViewModel by viewModel<PlaylistViewModel>()

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

        val pvModel: PlayerViewModel by viewModel { parametersOf(track) }
        playerViewModel = pvModel

        if(savedInstanceState != null) {
            currentTime = savedInstanceState.getString(
                TIME, getString(
                    R.string.start_time))
            binding.tvDuration.text = currentTime
        }
        val bottomSheetContainer = binding.playlistsBottomSheet
        val overlay = binding.overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }
        })

        adapter = SmallPlaylistAdapter { playlist ->
            val rootActivity = requireActivity() as RootActivity
            if (clickDebounce()) {
                if(playlist.playlistIdList.none { it == track?.trackId }) {
                    // Сохраняем трек в плейлисте
                    playlist.playlistIdList.add(track!!.trackId)
                    playerViewModel.updatePlaylist(playlist, track!!)
                } else {
                    val str = getString(R.string.already_added) + " " + playlist.playlistName
                    //bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    rootActivity.showMessage(str)
                }

            }
        }
        adapter.playlists = playlists

        binding.smallRecyclerView.adapter = adapter
        binding.smallRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // При загрузке сразу показываются плейлисты
        playlistViewModel.getPlaylists()

        // Переключение режимов экрана
        playlistViewModel.getPlaylistsLiveData().observe(viewLifecycleOwner) { list ->
            playlists.clear()
            playlists.addAll(list)
            adapter.notifyDataSetChanged()          // Выводим список
        }

        playerViewModel.getUpdatePlaylistsLiveData().observe(viewLifecycleOwner) {newState ->
            val rootActivity = requireActivity() as RootActivity
            if (newState.isNotEmpty()) {
                val str = getString(R.string.added_to_playlist) + " " + newState
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                rootActivity.showMessage(str)
            }
        }

        binding.playlistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Создание нового плей листа
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_playerFragment_to_createPlaylistFragment)
        }

        screenPreparation(track)    // Заполнение экрана

        // Нажатие кнопки Назад закрывает AudioPlayer
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Реакция на нажатие кнопки Play
        binding.playButton.setOnClickListener {
            playerViewModel.changeStatePlayerAfterClick()
        }

        // Реакция на нажатие кнопки Избранное
        binding.favoritesButton.setOnClickListener {
            track?.let { playerViewModel.onFavoriteClicked(it) }
            isChangedFavorites = true
        }

        // Получение данных от PlayerViewModel для кнопки Избранное
        playerViewModel.getStateFavoritesButtonLiveData().observe(viewLifecycleOwner) { newState ->
            favoritesButtonState = newState
            changeIconOfFavoritesButton (favoritesButtonState)
        }

        // Получение данных от PlayerViewModel
        playerViewModel.getStatePlayerLiveData().observe(viewLifecycleOwner) { newState ->
            playerState = newState
            playbackControl()
        }
    }

    private fun getTrack(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(EXTRA_TRACK, Track::class.java)
        } else {
            requireArguments().getSerializable(EXTRA_TRACK) as Track
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
            playerViewModel.pause()
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}