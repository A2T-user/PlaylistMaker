package com.a2t.myapplication.search.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2t.myapplication.R
import com.a2t.myapplication.databinding.FragmentSearchBinding
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.models.FilterScreenMode
import com.a2t.myapplication.search.ui.view_model.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

lateinit var screenMode: FilterScreenMode /* Режим экрана:      SEARCH - режим поиска
                                                                HISTORY - история поиска
                                                                NOTHING - ничего не найдено
                                                                ERROR - ошибка  */

private var inputString = ""
private const val INPUT_STRING = "INPUT_STRING"
private const val SEARCH_DEBOUNCE_DELAY = 2000L                 // Задержка поиска при вводе

class SearchFragment : Fragment()  {

    private val viewModel by viewModel<SearchViewModel>()
    private val tracks = arrayListOf<Track>()

    private lateinit var  adapter: TracksAdapter

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        adapter = TracksAdapter(requireContext(), viewModel)
        adapter.tracks = tracks
        binding.rvTrack.adapter = adapter
        binding.rvTrack.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        // Анимация обновления рециклера
        val animRecyclerView: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_revers_records)
        binding.rvTrack.layoutAnimation = animRecyclerView

        // При загрузке SearchActivity сразу показывается история поиска
        processingSearchHistory()


        // Переключение режимов экрана
        viewModel.getSearchLiveData().observe(viewLifecycleOwner) { newState ->
            screenMode = newState.screenMode
            changeScreenMode()
            val foundTracks = newState.foundTracks
            val errorMessage = newState.errorMessage
            when (screenMode) {
                FilterScreenMode.HISTORY ->  if (foundTracks != null)  showSearchHistory(foundTracks)
                FilterScreenMode.SEARCHING_RESULTS -> if (foundTracks != null)  showSearchingResults(foundTracks)
                FilterScreenMode.ERROR -> if (errorMessage != null) showError(errorMessage)
                FilterScreenMode.SEARCH, FilterScreenMode.NOTHING -> {}
            }
        }

        // Восстановление параметров
        if (savedInstanceState != null) {
            inputString = savedInstanceState.getString(INPUT_STRING, "")
            binding.searchEditText.setText(inputString)
        }


        // Нажатие на поле ввода
        binding.searchEditText.setOnClickListener {
            it.requestFocus()
            // Выводим клавиатуру
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }

        // Отслеживание изменений в поле поиск для включения и выключения кнопки Очистить
        binding.searchEditText.addTextChangedListener(
            afterTextChanged = { s: Editable? ->
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) processingSearchHistory()
                searchDebounce()
            }
        )

        // Нажатие кнопки Очистить
        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")          // Очищаем поле поиска
            // Убираем клавиатуру
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            processingSearchHistory()
        }

        // Нажатие кнопки Обновить
        binding.updateButton.setOnClickListener {
            processingRequest()
        }

        // Нажатие кнопки Search клавиатуры
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) processingRequest()
            false
        }

        // Реакция searchEditText на потерю фокуса.
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) processingSearchHistory()
        }

        // Нажатие кнопки Очистить историю
        binding.searchHistoryClearButton.setOnClickListener {
            viewModel.clearSearchHistory()
            processingSearchHistory()
        }
    }
    // Обработка запроса
    private fun processingRequest () {
        viewModel.processingRequest(binding.searchEditText.text.toString())
    }

    // Обработка истории поиска
    private fun processingSearchHistory (){
        viewModel.processingSearchHistory()
    }


    // Показ результатов поиска
    private fun showSearchingResults (foundTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(foundTracks)
        adapter.notifyDataSetChanged()          // Выводим список треков
        binding.rvTrack.scheduleLayoutAnimation()       // Анимация обновления строк рециклера
    }

    // Показ ошибки
    private fun showError(error: String) {
        binding.errorText.text = error
    }

    // Показ истории поиска
    private fun showSearchHistory (searchHistoryList: List<Track>){
        tracks.clear()
        tracks.addAll(searchHistoryList)
        adapter.notifyDataSetChanged()          // Выводим список треков
        if (tracks.isNotEmpty()) {              // История поиска не пуста
            binding.rvTrack.scheduleLayoutAnimation()   // Анимация обновления строк рециклера
        } else {                                // История поиска пуста
            binding.searchHistoryTitle.isVisible = false
            binding.searchHistoryClearButton.isVisible = false
        }
    }

    private var searchJob: Job? = null

    private fun searchDebounce() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            processingRequest()
        }
    }

    // Режим экрана (варианты компановки)
    private fun changeScreenMode () {
        when (screenMode) {
            FilterScreenMode.SEARCHING_RESULTS -> { // Результаты поиска
                binding.progressBar.isVisible = false               // Убираем progressBar
                binding.placeHolder.isVisible = false               // Убираем заглушку
                binding.rvContainer.isVisible = true                // Выводим контейнер рециклера
                binding.searchHistoryTitle.isVisible = false        // Убираем заголовок истории
                binding.searchHistoryClearButton.isVisible = false  // Убираем кнопку Очистить историю
            }
            FilterScreenMode.SEARCH -> {            // Поиска
                binding.placeHolder.isVisible = false               // Убираем заглушку
                binding.rvContainer.isVisible = false               // Убираем контейнер рециклера
                binding.searchHistoryTitle.isVisible = false        // Убираем заголовок истории
                binding.searchHistoryClearButton.isVisible = false  // Убираем кнопку Очистить историю
                binding.progressBar.isVisible = true                // Выводим progressBar
            }
            FilterScreenMode.HISTORY -> {           // История поиска
                binding.progressBar.isVisible = false               // Убираем progressBar
                binding.placeHolder.isVisible = false               // Убираем заглушку
                binding.rvContainer.isVisible = true                // Выводим контейнер рециклера
                binding.searchHistoryTitle.isVisible = true         // Выводим заголовок истории
                binding.searchHistoryClearButton.isVisible = true   // Выводим кнопку Очистить историю
            }
            FilterScreenMode.NOTHING -> {           // Заглушка "Ничего не нашлось"
                binding.progressBar.isVisible = false               // Убираем progressBar
                binding.rvContainer.isVisible = false               // Убираем контейнер рециклера
                binding.placeHolder.isVisible = true                // Выводим заглушку
                binding.updateButton.isVisible = false              // Убираем Кнопку обновить
                // Текст и рисунок: ничего не найдено
                binding.errorImage.setImageResource(R.drawable.ic_nothing_found)
                binding.errorText.setText(R.string.nothing_found)
            }
            FilterScreenMode.ERROR -> {             // Заглушка ошибка
                binding.progressBar.isVisible = false               // Убираем progressBar
                binding.rvContainer.isVisible = false               // Убираем контейнер рециклера
                binding.placeHolder.isVisible = true                // Выводим заглушку
                binding.updateButton.isVisible = true               // Выводим Кнопку обновить
                // Текст и рисунок: ошибка
                binding.errorImage.setImageResource(R.drawable.ic_errors)
            }
        }
    }

    // Сохранение папраметров
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_STRING, inputString)
    }
}