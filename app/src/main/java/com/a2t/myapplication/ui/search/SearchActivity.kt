package com.a2t.myapplication.ui.search

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
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.R
import com.a2t.myapplication.data.dto.SearchHistory
import com.a2t.myapplication.data.dto.TrackDto
import com.a2t.myapplication.data.dto.TracksResponse
import com.a2t.myapplication.data.network.ItunesApi
import com.a2t.myapplication.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


lateinit var screenMode: FilterScreenMode /* Режим экрана:      SEARCH - режим поиска
                                                                HISTORY - история поиска
                                                                NOTHING - ничего не найдено
                                                                ERROR - ошибка  */

private var inputString = ""
private const val INPUT_STRING = "INPUT_STRING"

private const val SEARCH_DEBOUNCE_DELAY = 2000L                 // Задержка поиска при вводе

class SearchActivity : AppCompatActivity() {
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val tracks = arrayListOf<Track>()

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ItunesApi::class.java)

    private val adapter = TracksAdapter(this@SearchActivity)

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
        showSearchHistory()

        // Анимация обновления рециклера
        val animRecyclerView: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(this,
            R.anim.layout_revers_records
        )
        rvTrack.layoutAnimation = animRecyclerView

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
                if (searchEditText.hasFocus() && s?.isEmpty() == true) showSearchHistory()
                searchDebounce()
            }
        )

        // Нажатие кнопки Очистить
        clearButton.setOnClickListener {
            searchEditText.setText("")          // Очищаем поле поиска
            // Убираем клавиатуру
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            showSearchHistory()
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

        /* Реакция searchEditText на потерю фокуса.
        В настоящее время абсолютно бесполезная вещь. Фокус с searchEditText никуда уйти не может,
        т.к. ни один экранный объект (кроме searchEditText) не имеет влюченного свойства focusableInTouchMode
        Возможно в будущем пригодится */
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) showSearchHistory()
        }

        // Нажатие кнопки Очистить историю
        searchHistoryClearButton.setOnClickListener {
            SearchHistory().clearSearchHistory()
            showSearchHistory()
        }

    }

    // Обработка запроса
    private fun processingRequest () {
        if (searchEditText.text.isNotEmpty()) {
            // Убираем все лишнее с экрана
            screenMode = FilterScreenMode.SEARCH
            changeScreenMode()
            iTunesService.search("song", searchEditText.text.toString()).enqueue(object :
                Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                    showResponse(response)              // Показываем результаты поиска
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showError()                         // Выводим заглушку с ошибкой
                }

            })
        }
    }

    // Обработка ответа
    private fun showResponse (response: Response<TracksResponse>) {
        tracks.clear()
        if (response.code() == 200) {
            if (response.body()?.results?.isNotEmpty() == true) {
                screenMode = FilterScreenMode.SEARCHING_RESULTS
                changeScreenMode()
                tracks.addAll(convertTrackDtoInTrack(response.body()?.results!!))
                adapter.notifyDataSetChanged()          // Выводим список треков
                rvTrack.scheduleLayoutAnimation()       // Анимация обновления строк рециклера
            } else {
                screenMode = FilterScreenMode.NOTHING
                changeScreenMode()
            }
        } else {
            // Выводим заглушку с ошибкой
            showError()
        }
    }

    // Показ истории поиска
    private fun showSearchHistory (){
        val searchHistoryList = SearchHistory().readSearchHistory()
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

    // Обработка ошибки
    private fun showError() {
        screenMode = FilterScreenMode.ERROR
        changeScreenMode()
    }

    // Сохранение папраметров
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_STRING, inputString)
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
            FilterScreenMode.SEARCH -> {    // Режим поиска
                placeHolder.isVisible = false               // Убираем заглушку
                rvContainer.isVisible = false               // Убираем контейнер рециклера
                searchHistoryTitle.isVisible = false        // Убираем заголовок истории
                searchHistoryClearButton.isVisible = false  // Убираем кнопку Очистить историю
                progressBar.isVisible = true                // Выводим progressBar
            }
            FilterScreenMode.HISTORY -> {  // История поиска
                progressBar.isVisible = false               // Убираем progressBar
                placeHolder.isVisible = false               // Убираем заглушку
                rvContainer.isVisible = true                // Выводим контейнер рециклера
                searchHistoryTitle.isVisible = true         // Выводим заголовок истории
                searchHistoryClearButton.isVisible = true   // Выводим кнопку Очистить историю
            }
            FilterScreenMode.NOTHING -> {  // Заглушка "Ничего не нашлось"
                progressBar.isVisible = false               // Убираем progressBar
                rvContainer.isVisible = false               // Убираем контейнер рециклера
                placeHolder.isVisible = true                // Выводим заглушку
                updateButton.isVisible = false              // Убираем Кнопку обновить
                // Текст и рисунок: ничего не найдено
                errorImage.setImageResource(R.drawable.ic_nothing_found)
                errorText.setText(R.string.nothing_found)
            }
            FilterScreenMode.ERROR -> {    // Заглушка ошибка
                progressBar.isVisible = false               // Убираем progressBar
                rvContainer.isVisible = false               // Убираем контейнер рециклера
                placeHolder.isVisible = true                // Выводим заглушку
                updateButton.isVisible = true               // Выводим Кнопку обновить
                // Текст и рисунок: ошибка
                errorImage.setImageResource(R.drawable.ic_errors)
                errorText.setText(R.string.communication_problems)
            }
        }
    }
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { processingRequest() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun convertTrackDtoInTrack (listDto: List<TrackDto>)= listDto.map {
        Track(
            it.trackId,
            it.trackName,
            it.artistName,
            it.collectionName,
            it.releaseDate,
            it.primaryGenreName,
            it.country,
            it.trackDurationInString(),
            it.artworkUrl100,
            it.getArtworkUrl512(),
            it.previewUrl
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }
}



