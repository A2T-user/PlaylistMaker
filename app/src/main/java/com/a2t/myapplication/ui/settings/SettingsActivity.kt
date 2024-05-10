package com.a2t.myapplication.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.a2t.myapplication.App
import com.a2t.myapplication.R
import com.a2t.myapplication.ui.main.SWITCHER_KEY
import com.a2t.myapplication.ui.main.sharedPrefs


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrow = findViewById<ImageView>(R.id.iv_arrow)
        val tvSend = findViewById<TextView>(R.id.tv_send)
        val tvSupport = findViewById<TextView>(R.id.tv_support)
        val tvUserAgreement = findViewById<TextView>(R.id.tv_user_agreement)
        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitcher)

        // Устанавливаем значение Switcher из сохраненного
        themeSwitcher.setChecked(sharedPrefs.getBoolean(SWITCHER_KEY, false))

        // Закрытие SettingsActivity по стрелке
        arrow.setOnClickListener {
            finish()
        }

        // Темная тема
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            // Меняем тему приложения согласно положению Switcher
            (applicationContext as App).switchTheme(checked)
            // Сохраняем новое значение Switcher
            sharedPrefs.edit()
                .putBoolean(SWITCHER_KEY, checked)
                .apply()
        }

        // Поделиться приложением
        tvSend.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_android))
            val chooserIntent = Intent.createChooser(intent, getString(R.string.mail))
            startActivity(chooserIntent)
        }

        // Техподдержка
        tvSupport.setOnClickListener {
            val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.e_mail)))
            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
            email.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_thank_you))
            startActivity(Intent.createChooser(email, getString(R.string.mail)))
        }

        // Пользовательское соглашение
        tvUserAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer)))
            startActivity(browserIntent)
        }
    }
}