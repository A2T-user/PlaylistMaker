package com.a2t.myapplication.main.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.a2t.myapplication.App
import com.a2t.myapplication.R
import com.a2t.myapplication.main.ui.view_model.MainActivityState
import com.a2t.myapplication.main.ui.view_model.MainViewModel
import com.a2t.myapplication.mediateca.ui.activity.MediatecaActivity
import com.a2t.myapplication.search.ui.activity.SearchActivity
import com.a2t.myapplication.settings.domain.api.SettingsInteractor
import com.a2t.myapplication.settings.ui.activity.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.getKoin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModel<MainViewModel>()

        setContentView(R.layout.activity_main)

        // Устанавливаем тему приложения согласно сохраненных параметров
        val settingsInteractor:SettingsInteractor = getKoin().get()
        (applicationContext as App).switchTheme(settingsInteractor.getThemeSetting())

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnMedia = findViewById<Button>(R.id.btn_media)
        val btnSettings = findViewById<Button>(R.id.btn_settings)

        // Кнопка ПОИСК
        btnSearch.setOnClickListener {
            viewModel.clickSearch()
        }
        // Кнопка МЕДИАТЕКА
        btnMedia.setOnClickListener {
            viewModel.clickMediateca()
        }
        // Кнопка НАСТРОЙКИ
        btnSettings.setOnClickListener {
            viewModel.clickSettings()
        }

        viewModel.getStateLiveData().observe(this) { newState ->
            when(newState) {
                MainActivityState.SEARCH -> {
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    startActivity(intent)
                }
                MainActivityState.MEDIATECA -> {
                    val intent = Intent(this@MainActivity, MediatecaActivity::class.java)
                    startActivity(intent)
                }
                MainActivityState.SETTINGS -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                }
                MainActivityState.NOTHING -> {}
            }
            viewModel.nothing()
        }
    }
}