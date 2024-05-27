package com.a2t.myapplication.search.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.R
import com.a2t.myapplication.search.ui.models.FilterScreenMode
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.models.SearchData
import com.a2t.myapplication.search.ui.view_model.SearchViewModel

lateinit var screenMode: FilterScreenMode /* Режим экрана:      SEARCH - режим поиска
                                                                HISTORY - история поиска
                                                                NOTHING - ничего не найдено
                                                                ERROR - ошибка  */

lateinit var searchScreenState: SearchData // Данные получаемые из viewModel

private var inputString = ""
private const val INPUT_STRING = "INPUT_STRING"
private const val SEARCH_DEBOUNCE_DELAY = 2000L                 // Задержка поиска при вводе

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private val tracks = arrayListOf<Track>()

    private lateinit var  adapter: TracksAdapter

    private lateinit var searchEditText: EditText           // Поле поиска
    private lateinit var placeHolder: LinearLayout          // Заглушка
    private lateinit var errorImage: ImageView              // Рисунок заглушки
    private lateinit var errorText: TextView                // Текст заглушки
    private lateinit var updateButton: TextView             // Кнопка Обновить
    private lateinit var progressBar: ProgressBar           // Прогресс бар
    private lateinit var rvContainer: ScrollView            // Контейнер рециклера
    private lateinit var searchHistoryTitle: TextView       // Текст истории
    private lateinit var rvTrack: RecyclerView              // Рециклер
    private lateinit var searchHistoryClearButton: TextView // Кнопка Очистить историю

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory(this))[SearchViewModel::class.java]
        adapter = TracksAdapter(this@SearchActivity, viewModel)

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val arrow = findViewById<ImageView>(R.id.iv_arrow)                      // Стрелка
        val clearButton = findViewById<LinearLayout>(R.id.clearIcon)            // Кнопка очистки поя поиска
        rvContainer = findViewById(R.id.rvContainer)                            // Контейнер рециклера
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)              // Текст истории
        searchHistoryClearButton = findViewById(R.id.searchHistoryClearButton)  // Кнопка Очистить историю
        searchEditText = findViewById(R.id.searchEditText)                      // Поле поиска
        progressBar = findViewById(R.id.progressBar)                            // Прогрессбар
        rvTrack = findViewById(R.id.rvTrack)                                    // Рециклер
        placeHolder = findViewById(R.id.placeHolder)                            // Макет заплатки
        errorImage = findViewById(R.id.errorImage)                              // Иконка ошибки
        errorText = findViewById(R.id.errorText)                                // Текст ошибки
        updateButton = findViewById(R.id.updateButton)                          // Кнопка Обновить

        adapter.tracks = tracks

        rvTrack.adapter = adapter

        // При загрузке SearchActivity сразу показывается история поиска
        processingSearchHistory()

        // Анимация обновления рециклера
        val animRecyclerView: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(this,
            R.anim.layout_revers_records
        )
        rvTrack.layoutAnimation = animRecyclerView

        // Переключение режимов экрана
        viewModel.getSearchLiveData().observe(this) { newState ->
            searchScreenState = newState
            screenMode = searchScreenState.screenMode
            val foundTracks = searchScreenState.foundTracks
            val errorMessage = searchScreenState.errorMessage
            when (screenMode) {
                FilterScreenMode.HISTORY ->  if (foundTracks != null)  showSearchHistory(foundTracks)
                FilterScreenMode.SEARCH -> showSearch()
                FilterScreenMode.SEARCHING_RESULTS -> if (foundTracks != null)  showSearchingResults(foundTracks)
                FilterScreenMode.NOTHING -> showSearchingNothing()
                FilterScreenMode.ERROR -> if (errorMessage != null) showError(errorMessage)
            }
        }
        
        // Восстановление параметров
        if (savedInstanceState != null) {
            inputString = savedInstanceState.getString(INPUT_STRING, "")
            searchEditText.setText(inputString)
        }

        // Переход в главное окно приложения
        arrow.setOnClickListener {
            finish()
        }

        // Нажатие на поле ввода
        searchEditText.setOnClickListener {
            it.requestFocus()
            // Выводим клавиатуру
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }

        // Отслеживание изменений в поле поиск для включения и выключения кнопки Очистить
        searchEditText.addTextChangedListener(
            afterTextChanged = { s: Editable? ->
                clearButton.isVisible = !s.isNullOrEmpty()
                if (searchEditText.hasFocus() && s?.isEmpty() == true) processingSearchHistory()
                searchDebounce()
            }
        )

        // Нажатие кнопки Очистить
        clearButton.setOnClickListener {
            searchEditText.setText("")          // Очищаем поле поиска
            // Убираем клавиатуру
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            processingSearchHistory()
        }

        // Нажатие кнопки Обновить
        updateButton.setOnClickListener {
            processingRequest()
        }

        // Нажатие кнопки Search клавиатуры
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) processingRequest()
            false
        }

        // Реакция searchEditText на потерю фокуса.
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) processingSearchHistory()
        }

        // Нажатие кнопки Очистить историю
        searchHistoryClearButton.setOnClickListener {
            viewModel.clearSearchHistory()
            processingSearchHistory()
        }

    }

    // Обработка запроса
    private fun processingRequest () {
        viewModel.processingRequest(searchEditText.text.toString())
    }

    // Обработка истории поиска
    fun processingSearchHistory (){
        viewModel.processingSearchHistory()
    }

    // Показ поиска (прогрессбар)
    private fun showSearch () {
        screenMode = FilterScreenMode.SEARCH
        changeScreenMode()
    }

    // Показ результатов поиска
    private fun showSearchingResults (foundTracks: List<Track>) {
        screenMode = FilterScreenMode.SEARCHING_RESULTS
        changeScreenMode()
        tracks.clear()
        tracks.addAll(foundTracks)
        adapter.notifyDataSetChanged()          // Выводим список треков
        rvTrack.scheduleLayoutAnimation()       // Анимация обновления строк рециклера
    }

    // Показ результатов поиска, если ничего не найдено
    private fun showSearchingNothing () {
        screenMode = FilterScreenMode.NOTHING
        changeScreenMode()
    }

    // Показ ошибки
    private fun showError(error: String) {
        screenMode = FilterScreenMode.ERROR
        changeScreenMode()
        errorText.text = error
    }

    // Показ истории поиска
    private fun showSearchHistory (searchHistoryList: List<Track>){
        screenMode = FilterScreenMode.HISTORY
        changeScreenMode()
        tracks.clear()
        tracks.addAll(searchHistoryList)
        adapter.notifyDataSetChanged()          // Выводим список треков
        if (tracks.isNotEmpty()) {              // История поиска не пуста
            rvTrack.scheduleLayoutAnimation()   // Анимация обновления строк рециклера
        } else {                                // История поиска пуста
            searchHistoryTitle.isVisible = false
            searchHistoryClearButton.isVisible = false
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { processingRequest() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    // Режим экрана (варианты компановки)
    private fun changeScreenMode () {
        when (screenMode) {
            FilterScreenMode.SEARCHING_RESULTS -> { // Результаты поиска
                progressBar.isVisible = false               // Убираем progressBar
                placeHolder.isVisible = false               // Убираем заглушку
                rvContainer.isVisible = true                // Выводим контейнер рециклера
                searchHistoryTitle.isVisible = false        // Убираем заголовок истории
                searchHistoryClearButton.isVisible = false  // Убираем кнопку Очистить историю
            }
            FilterScreenMode.SEARCH -> {            // Поиска
                placeHolder.isVisible = false               // Убираем заглушку
                rvContainer.isVisible = false               // Убираем контейнер рециклера
                searchHistoryTitle.isVisible = false        // Убираем заголовок истории
                searchHistoryClearButton.isVisible = false  // Убираем кнопку Очистить историю
                progressBar.isVisible = true                // Выводим progressBar
            }
            FilterScreenMode.HISTORY -> {           // История поиска
                progressBar.isVisible = false               // Убираем progressBar
                placeHolder.isVisible = false               // Убираем заглушку
                rvContainer.isVisible = true                // Выводим контейнер рециклера
                searchHistoryTitle.isVisible = true         // Выводим заголовок истории
                searchHistoryClearButton.isVisible = true   // Выводим кнопку Очистить историю
            }
            FilterScreenMode.NOTHING -> {           // Заглушка "Ничего не нашлось"
                progressBar.isVisible = false               // Убираем progressBar
                rvContainer.isVisible = false               // Убираем контейнер рециклера
                placeHolder.isVisible = true                // Выводим заглушку
                updateButton.isVisible = false              // Убираем Кнопку обновить
                // Текст и рисунок: ничего не найдено
                errorImage.setImageResource(R.drawable.ic_nothing_found)
                errorText.setText(R.string.nothing_found)
            }
            FilterScreenMode.ERROR -> {             // Заглушка ошибка
                progressBar.isVisible = false               // Убираем progressBar
                rvContainer.isVisible = false               // Убираем контейнер рециклера
                placeHolder.isVisible = true                // Выводим заглушку
                updateButton.isVisible = true               // Выводим Кнопку обновить
                // Текст и рисунок: ошибка
                errorImage.setImageResource(R.drawable.ic_errors)
            }
        }
    }

    // Сохранение папраметров
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_STRING, inputString)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }
}



