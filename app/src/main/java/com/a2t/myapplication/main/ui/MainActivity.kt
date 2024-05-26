package com.a2t.myapplication.main.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.a2t.myapplication.App
import com.a2t.myapplication.R
import com.a2t.myapplication.creator.SettingsCreator
import com.a2t.myapplication.mediateca.ui.MediatecaActivity
import com.a2t.myapplication.search.ui.activity.SearchActivity
import com.a2t.myapplication.settings.ui.activity.SettingsActivity

lateinit var sharedPrefs: SharedPreferences

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"     // Имя файла для сохраняемых параметров
const val SEARCH_HISTORY_KEY = "search_history"                             // Ключ для истории поиска

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем экземпляр Shared Preferences
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        // Устанавливаем тему приложения согласно сохраненных параметров
        val settingsInteractor = SettingsCreator.provideSettingsInteractor()
        (applicationContext as App).switchTheme(settingsInteractor
            .getThemeSetting())

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnMedia = findViewById<Button>(R.id.btn_media)
        val btnSettings = findViewById<Button>(R.id.btn_settings)

        // Кнопка ПОИСК
        btnSearch.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }
        // Кнопка МЕДИАТЕКА
        btnMedia.setOnClickListener {
            val intent = Intent(this@MainActivity, MediatecaActivity::class.java)
            startActivity(intent)
        }
        // Кнопка НАСТРОЙКИ
        btnSettings.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

    }
}