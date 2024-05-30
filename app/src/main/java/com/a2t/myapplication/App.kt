package com.a2t.myapplication

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

lateinit var appContext: Context
class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}

