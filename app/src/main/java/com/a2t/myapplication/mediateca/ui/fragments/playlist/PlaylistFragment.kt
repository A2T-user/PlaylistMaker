package com.a2t.myapplication.mediateca.ui.fragments.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.a2t.myapplication.R
import com.a2t.myapplication.mediateca.ui.view_model.PlaylistViewModel
import com.a2t.myapplication.databinding.FragmentPlaylistBinding
import com.a2t.myapplication.showplaylist.ui.fragment.ShowPlaylistFragment
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CLICK_DEBOUNCE_DELAY = 1000L

class PlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistFragment()
    }
    private lateinit var binding: FragmentPlaylistBinding

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var  adapter: PlaylistAdapter

    private val playlists = arrayListOf<Playlist>()

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Создание нового плей листа
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediatecaFragment_to_createPlaylistFragment)
        }

        adapter = PlaylistAdapter {
            if (clickDebounce()) {
                // Открыть Playlist
                findNavController().navigate(R.id.action_mediatecaFragment_to_showPlaylistFragment,
                    ShowPlaylistFragment.createArgs(it))
            }
        }
        adapter.playlists = playlists

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // При загрузке сразу показываются плейлисты
        viewModel.getPlaylists()

        // Переключение режимов экрана
        viewModel.getPlaylistsLiveData().observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) showPlaceholder() else showPlaylists(list)
        }
    }

    private fun showPlaceholder () {
        binding.recyclerView.isVisible = false
        binding.emptyImage.isVisible = true
        binding.emptyText.isVisible = true
    }

    private fun showPlaylists (list: List<Playlist>) {
        binding.recyclerView.isVisible = true
        binding.emptyImage.isVisible = false
        binding.emptyText.isVisible = false
        playlists.clear()
        playlists.addAll(list)
        adapter.notifyDataSetChanged()          // Выводим список треков
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
}