package com.a2t.myapplication

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random


class SearchActivity : AppCompatActivity()

{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrow = findViewById<ImageView>(R.id.iv_arrow)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

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

        // Нажатие на кнопку очистить
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
        }

        // Отслеживание изменений в поле поиск
        searchEditText.addTextChangedListener(
            afterTextChanged = {s: Editable? ->
                clearButton.isVisible = !s.isNullOrEmpty()
                inputString = searchEditText.text.toString()
                startSearch(inputString, progressBar)
            }
        )

        val rvTrack = findViewById<RecyclerView>(R.id.rvTrack)
        rvTrack.adapter = trackAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUTSTRING, inputString)
    }

    private fun startSearch (inputString: String, progressBar: ProgressBar) {
        progressBar.isVisible = true
        // Задержка для иммитации поиска
        Handler().postDelayed({
            progressBar.isVisible = false
            searchList.clear()
            // Если поисковая строка пуста, то и рециклер пуст
            if (inputString.isNotEmpty()) searchList.addAll(getSearchList())
            trackAdapter.notifyDataSetChanged()
        }, 1000)
    }

    // Возвращает масив с результатами поиска
    // Для иммитации результатов поиска, список каждый раз выводится в случайном порядке
    private fun getSearchList (): MutableList<Track> {
        val originalList = mutableListOf<Track>()
        originalList.addAll(trackList)
        val resultingList = mutableListOf<Track>()
        val rand = Random(System.nanoTime())
        while (originalList.isNotEmpty()) {
            val it = originalList[(0..< originalList.size).random(rand)]
            resultingList.add(it)
            originalList.remove(it)
        }

        return resultingList
    }
}

private var inputString = ""
private const val INPUTSTRING = "INPUT_STRING"
private val searchList: MutableList<Track> = mutableListOf()
private val trackAdapter = TrackAdapter(searchList)


val trackList = listOf(
    Track(
        "Smells Like Teen Spirit",
        "Nirvana", "5:01",
        "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
    Track(
        "Billie Jean",
        "Michael Jackson", "4:35",
        "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
    Track(
        "Stayin' Alive",
        "Bee Gees", "4:10",
        "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
    Track(
        "Whole Lotta Love",
        "Led Zeppelin", "5:33",
        "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
    Track(
        "Sweet Child O'Mine",
        "Guns N' Roses", "5:03",
        "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg "),
)