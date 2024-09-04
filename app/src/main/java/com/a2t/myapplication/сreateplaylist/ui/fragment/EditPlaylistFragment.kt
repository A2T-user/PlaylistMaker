package com.a2t.myapplication.сreateplaylist.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.a2t.myapplication.R
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.a2t.myapplication.сreateplaylist.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment: CreatePlaylistFragment() {

    companion object {

        private const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"       // Тег для трека

        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(EXTRA_PLAYLIST to playlist)
    }


    override val viewModel by viewModel<EditPlaylistViewModel>()

    private var playlist: Playlist? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = getPlaylist()

        // Обновление строк заголовка и кнопки
        binding.tvTitle.text = getString(R.string.edit_playlist)
        binding.createButton.text = getString(R.string.save)

        // Заполнение полей исходными данными

        binding.etName.setText(playlist?.playlistName)
        binding.etDescription.setText(playlist?.playlistDescription)
        showCover(playlist?.playlistUri?.toUri())


    }

    override fun savePlaylist(strPlayListUri: String) {
        viewModel.savePlaylist(playlist?.playlistId, playlist?.playlistUri, playlist?.playlistIdList)
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun getPlaylist(): Playlist? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(EXTRA_PLAYLIST, Playlist::class.java)
        } else {
            requireArguments().getSerializable(EXTRA_PLAYLIST) as Playlist
        }
    }
}