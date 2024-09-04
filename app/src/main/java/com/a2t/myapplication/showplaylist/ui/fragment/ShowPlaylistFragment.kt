package com.a2t.myapplication.showplaylist.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.FragmentShowPlaylistBinding
import com.a2t.myapplication.player.ui.fragment.PlayerFragment
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.showplaylist.ui.view_model.ShowPlaylistViewModel
import com.a2t.myapplication.util.DifferentStrings
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.a2t.myapplication.сreateplaylist.ui.fragment.EditPlaylistFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CLICK_DEBOUNCE_DELAY = 1000L

class ShowPlaylistFragment: Fragment()  {

    companion object {
        private const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"       // Тег для плейлиста

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(EXTRA_PLAYLIST to playlistId)
    }

    private val showPlaylistViewModel by viewModel<ShowPlaylistViewModel>()
    private lateinit var binding: FragmentShowPlaylistBinding
    private var playlistId: Long = 0L
    private var playlist: Playlist? = null
    private lateinit var adapter: PlaylistTracksAdapter
    private var isClickAllowed = true
    private val tracks = arrayListOf<Track>()
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = requireArguments().getLong(EXTRA_PLAYLIST)


        showPlaylistViewModel.getPlaylistById(playlistId)

        // Заполнение экрана
        showPlaylistViewModel.getShowPlaylistsLiveData().observe(viewLifecycleOwner) {
            playlist = it
            if (playlist != null) bind(playlist!!)            // Заполняем поля плейлиста
        }

        // Заполнение рециклера
        showPlaylistViewModel.getPlaylistTracksLiveData().observe(viewLifecycleOwner) { tracklist ->
            binding.tvCountTracks.text = DifferentStrings.countTracks(tracklist.size)
            binding.tvCountTracksSmall.text = DifferentStrings.countTracks(tracklist.size)
            binding.playlistDuration.text = playlistDuration(tracklist)
            showTracks(tracklist)
        }

        // Нажатие кнопки Назад закрывает фрагмент
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // BOTTOMSHEET*******************************************
        // BOTTOMSHEET PLAYLIST
        val bottomSheetContainer = binding.showPlaylistsBottomSheet
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

        // BOTTOMSHEET MENU
        val menuBottomSheetContainer = binding.menuBottomSheet
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility = View.VISIBLE
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }
        })

        adapter = PlaylistTracksAdapter (
            { track ->
                if (clickDebounce()) {
                    // Открыть AudioPlayer
                    findNavController().navigate(R.id.action_showPlaylistFragment_to_playerFragment,
                        PlayerFragment.createArgs(track))
                }
            },
            { track ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_track))                                                              // Заголовок диалога
                    .setMessage(getString(R.string.are_you_sure))                                                            // Описание диалога
                    .setNeutralButton(getString(R.string.сancel)) { dialog, which -> }                                       // Добавляет кнопку «Отмена»
                    .setPositiveButton(getString(R.string.delete)) { dialog, which ->                                        // Добавляет кнопку «Завершить»
                        playlist?.let {
                            tracks.remove(track)
                            showPlaylistViewModel.updatePlaylist(it, track) }                                    // Обновляем плей лист в таблице
                        // Проверяем, есть ли трек в других плейлистах. Если нет удаляем его из таблицы
                        showPlaylistViewModel.deleteTrack(track.trackId)
                        playlist?.playlistIdList?.let { showPlaylistViewModel.getPlaylistTrackList(it) }                     // Обновляем экран
                    }
                    .show()
                true
            }
        )

        adapter.tracks = tracks
        binding.rvPlaylistTracks.adapter = adapter
        binding.rvPlaylistTracks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Кнопка Поделиться
        binding.ivSend.setOnClickListener {
            sendPlaylist ()
        }
        // Кнопка Меню
        binding.ivMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // МЕНЮ**************************************

        // Пункт Поделиться
        binding.menuSend.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN    // Скрываем меню
            sendPlaylist ()
        }

        // Пункт Редактирование
        binding.menuEdit.setOnClickListener {
            // Открыть EditPlaylistFragment
            findNavController().navigate(R.id.action_showPlaylistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlist!!))

        }

        // Пункт Удалить
        binding.menuDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN    // Скрываем меню
            deletePlaylist()
        }
    }

    // Удалить плейлист
    private fun deletePlaylist () {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist))                                 // Заголовок диалога
            .setMessage(getString(R.string.want_delete_playlist) + " \"" + playlist?.playlistName + "\"?")                          // Описание диалога
            .setNegativeButton(getString(R.string.no)) { dialog, which -> }                // Добавляет кнопку «Нет»
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->                 // Добавляет кнопку «Да»
                // Создаем копию списка id треков плейлиста
                val idTracks = mutableListOf<Int>()
                idTracks.addAll(playlist!!.playlistIdList)
                showPlaylistViewModel.deletePlaylistById(playlistId, idTracks)              // Удаляем плейлист
                requireActivity().onBackPressedDispatcher.onBackPressed()                   // Закрываем окно Плейлист
            }
            .show()
    }

    // Отправка плей листа
    private fun sendPlaylist () {
        if (tracks.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.attention))                        // Заголовок диалога
                .setMessage(getString(R.string.nothing_to_share))               // Описание диалога
                .setNegativeButton(getString(R.string.ok)) { dialog, which -> } // Добавляет кнопку «Ok»
                .show()
        } else {
            // Формируем строку для отправки
            var strToSend = playlist?.playlistName + "\n"+
                    (playlist?.playlistDescription?: "") + "\n" +
                    DifferentStrings.countTracks(tracks.size) + "\n"
            for (index in tracks.indices){
                val track = tracks[index]
                val str = "\n" + (index + 1).toString() + ". " + track.artistName + " '" + track.trackName + "' " + track.trackTime
                strToSend += str
            }
            // Отправляем
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, strToSend)
            val chooserIntent = Intent.createChooser(intent, getString(R.string.mail))
            startActivity(chooserIntent)
        }
    }



    // Заполняем поля плейлиста
    private fun bind (playlist: Playlist) {
        // Основной экран
        Glide.with(this)
            .load(playlist.playlistUri)
            .placeholder(R.drawable.ic_album_big)
            .centerCrop()
            .into(binding.playlistImage)
        binding.playlistName.text = playlist.playlistName
        binding.playlistDescription.text = playlist.playlistDescription

        // Меню
        Glide.with(this)
            .load(playlist.playlistUri)
            .placeholder(R.drawable.ic_album)
            .centerCrop()
            .into(binding.ivCoverSmall)
        binding.tvNameSmall.text = playlist.playlistName
    }

    // Получение строки для поля Продолжительность плейлиста
    private fun playlistDuration (tracklist: List<Track>): String {
        var duration =0.0f
        for (track: Track in tracklist) {
            val srtList = track.trackTime.split(":")
            val min = srtList[0].toInt()
            val sec = srtList[1].toInt()
            duration += min + sec / 60.0f
        }
        val durationToInt = duration.toInt()
        return DifferentStrings.countMinutes(durationToInt)
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
    override fun onStop() {
        super.onStop()
        isClickAllowed = true
    }

    override fun onStart() {
        super.onStart()
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN    // Скрываем меню
    }

    private fun showTracks (tracklist: List<Track>) {
        if (tracklist.isEmpty()) {
            binding.rvPlaylistTracks.isVisible = false
            binding.tvNoTracks.isVisible = true
        } else {
            binding.tvNoTracks.isVisible = false
            binding.rvPlaylistTracks.isVisible = true
            tracks.clear()
            tracks.addAll(tracklist)
            adapter.notifyDataSetChanged()          // Выводим список треков
        }
    }
}