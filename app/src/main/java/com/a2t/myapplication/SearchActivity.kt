package com.a2t.myapplication

import android.os.Bundle
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrow = findViewById<ImageView>(R.id.iv_arrow)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
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
                if (inputString.isNotEmpty()) startSearch()

            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUTSTRING, inputString)
    }

    private fun startSearch () {
        Toast.makeText(this@SearchActivity, getString(R.string.search_text), Toast.LENGTH_SHORT).show()
    }
}

private var inputString = ""
private val INPUTSTRING = "INPUT_STRING"