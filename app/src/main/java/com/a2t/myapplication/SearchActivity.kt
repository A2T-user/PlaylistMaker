package com.a2t.myapplication

import android.os.Bundle
import android.text.Editable
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ItunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private val adapter = TracksAdapter()

    private lateinit var searchEditText: EditText
    private lateinit var placeHolder: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var updateButton: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvTrack: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrow = findViewById<ImageView>(R.id.iv_arrow)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)           // Кнопка очистки поя поиска
        searchEditText = findViewById(R.id.searchEditText)                  // Поле поиска
        progressBar = findViewById(R.id.progressBar)                        // Прогрессбар
        rvTrack = findViewById(R.id.rvTrack)                                // Рециклер
        placeHolder = findViewById(R.id.placeHolder)                        // Макет заплатки
        errorImage = findViewById(R.id.errorImage)                          // Иконка ошибки
        errorText = findViewById(R.id.errorText)                            // Текст ошибки
        updateButton = findViewById(R.id.updateButton)                      // Кнопка Обновить

        adapter.tracks = tracks

        rvTrack.adapter = adapter

        // Анимация обновления рециклера
        val animRecyclerView: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_revers_records)
        rvTrack.layoutAnimation = animRecyclerView

        // Восстановление параметров
        if (savedInstanceState != null) {
            inputString = savedInstanceState.getString(INPUTSTRING, "")
            searchEditText.setText(inputString)
        }

        // Переход в главное окно приложения
        arrow.setOnClickListener {
            finish()
        }

        // Нажатие на поле ввода
        searchEditText.setOnClickListener {
            it.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }

        // Отслеживание изменений в поле поиск для включения и выключения кнопки Очистить
        searchEditText.addTextChangedListener(
            afterTextChanged = { s: Editable? ->
                clearButton.isVisible = !s.isNullOrEmpty()
            }
        )

        // Нажатие кнопки Очистить
        clearButton.setOnClickListener {
            searchEditText.setText("")          // Очищаем поле поиска
            // Убираем клавиатуру
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            // Убираем заглушку
            updateButton.isVisible = false
            placeHolder.isVisible = false
            // Очищаем рециклер
            tracks.clear()
            adapter.notifyDataSetChanged()
        }

        // Нажатие кнопки Обновить
        updateButton.setOnClickListener {
            processingRequest()
        }

        // Нажатие кнопки Done клавиатуры
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                processingRequest()
                true
            }
            false
        }

    }

    // Обработка запроса
    private fun processingRequest () {
        if (searchEditText.text.isNotEmpty()) {
            progressBar.isVisible = true
            iTunesService.search("song", searchEditText.text.toString()).enqueue(object :
                Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                    progressBar.isVisible = false
                    showResponse(response)
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    progressBar.isVisible = false
                    showError()             // Выводим заглушку с ошибкой
                }

            })
        }
    }

    // Обработка ответа
    private fun showResponse (response: Response<TracksResponse>) {
        tracks.clear()
        if (response.code() == 200) {
            if (response.body()?.results?.isNotEmpty() == true) {
                tracks.addAll(response.body()?.results!!)
                rvTrack.isVisible = true                // Показать рециклер
                adapter.notifyDataSetChanged()          // Выводим список треков
                rvTrack.scheduleLayoutAnimation()       // Анимация обновления строк рециклера
            }
            if (tracks.isEmpty()) {
                // Выводим заглушку
                placeHolder.isVisible = true            // Выводим заглушку
                // Текст и рисунок: ничего не найдено
                errorImage.setImageResource(R.drawable.ic_nothing_found)
                errorText.setText(R.string.nothing_found)
                updateButton.isVisible = false          // Кнопка обновить скрыта
            } else {
                // Скрываем заглушку
                placeHolder.isVisible = false
            }
        } else {
            // Выводим заглушку с ошибкой
            showError()
        }
    }

    // Обработка ошибки
    private fun showError() {
        adapter.notifyDataSetChanged()          // Пустой рециклер
        rvTrack.isVisible = false               // Скрыть рециклер
        placeHolder.isVisible = true            // Выводим заглушку
        // Текст и рисунок: ничего не найдено
        errorImage.setImageResource(R.drawable.ic_errors)
        errorText.setText(R.string.communication_problems)
        updateButton.isVisible = true          // Кнопка обновить на экране
    }

    // Сохранение папраметров
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUTSTRING, inputString)
    }
}

private var inputString = ""
private const val INPUTSTRING = "INPUT_STRING"


