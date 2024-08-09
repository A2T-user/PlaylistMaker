package com.a2t.myapplication.mediateca.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2t.myapplication.R
import com.a2t.myapplication.mediateca.ui.view_model.FavoritesViewModel
import com.a2t.myapplication.databinding.FragmentFavoritesBinding
import com.a2t.myapplication.player.ui.activity.PlayerActivity
import com.a2t.myapplication.player.ui.activity.isChangedFavorites
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.fragment.TracksAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CLICK_DEBOUNCE_DELAY = 1000L

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private lateinit var  adapter: TracksAdapter
    private lateinit var binding: FragmentFavoritesBinding

    private val viewModel by viewModel<FavoritesViewModel>()

    private val tracks = arrayListOf<Track>()
    private var isClickAllowed = true



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TracksAdapter {
            if (clickDebounce()) {
                // Открыть AudioPlayer
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("EXTRA_TRACK", it)
                startActivity(intent)
            }
        }

        adapter.tracks = tracks
        binding.rvFavoritesTracks.adapter = adapter
        binding.rvFavoritesTracks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        // Анимация обновления рециклера
        val animRecyclerView: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_revers_records)
        binding.rvFavoritesTracks.layoutAnimation = animRecyclerView

        // При загрузке сразу показывается Избранное
        viewModel.getFavorites()

        // Переключение режимов экрана
        viewModel.getFavoritesLiveData().observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) showPlaceholder() else showFavorites()
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

    // Показ Избранного
    private fun showPlaceholder(){
        binding.rvFavoritesTracks.isVisible = false
        binding.emptyImage.isVisible = true
        binding.emptyTextView.isVisible = true
    }
    // Показ заглушки
    private fun showFavorites(){
        binding.rvFavoritesTracks.isVisible = true
        binding.emptyImage.isVisible = false
        binding.emptyTextView.isVisible = false
    }

    override fun onStart() {
        super.onStart()
        if (isChangedFavorites) {      // Если была нажата кнопка Избранное Плеера - возможно изменение в Избранном
            viewModel.getFavorites()   // Повторить запрос к БД на извлечение списка
        }
        isChangedFavorites = false
    }
}