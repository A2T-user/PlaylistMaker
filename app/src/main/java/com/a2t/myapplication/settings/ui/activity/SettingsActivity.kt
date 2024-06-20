package com.a2t.myapplication.settings.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.a2t.myapplication.App
import com.a2t.myapplication.R
import com.a2t.myapplication.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrow = findViewById<ImageView>(R.id.iv_arrow)
        val tvSend = findViewById<TextView>(R.id.tv_send)
        val tvSupport = findViewById<TextView>(R.id.tv_support)
        val tvUserAgreement = findViewById<TextView>(R.id.tv_user_agreement)
        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitcher)

        // Устанавливаем значение Switcher из сохраненного
        var oldDarkTheme = viewModel.getDarkTheme()
        themeSwitcher.setChecked(oldDarkTheme)

        // Закрытие SettingsActivity по стрелке
        arrow.setOnClickListener {
            finish()
        }

        // Темная тема
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            // Сохраняем новое значение Switcher
            viewModel.updateThemeLiveData(checked)

        }
        viewModel.getThemeLiveData().observe(this) { newDarkTheme ->
            if (oldDarkTheme != newDarkTheme) {
                (applicationContext as App).switchTheme(newDarkTheme)
                oldDarkTheme = newDarkTheme
            }
        }


        // Поделиться приложением
        tvSend.setOnClickListener {
            viewModel.updateSharingLiveData(Intent.ACTION_SEND)
        }

        // Техподдержка
        tvSupport.setOnClickListener {
            viewModel.updateSharingLiveData(Intent.ACTION_SENDTO)
        }

        // Пользовательское соглашение
        tvUserAgreement.setOnClickListener {
            viewModel.updateSharingLiveData(Intent.ACTION_VIEW)
        }

        viewModel.getSharingLiveData().observe(this) {
            if (it.intentAction != null) {
                when (it.intentAction) {
                    Intent.ACTION_SEND -> shareLink(it.strExtra)        // Поделиться приложением
                    Intent.ACTION_SENDTO-> openSupport(it.strExtra)     // Техподдержка
                    else -> openTerms(it.strExtra)                      // Пользовательское соглашение
                }
            }
        }
    }

    // Поделиться приложением
    private fun shareLink (strExtra: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, strExtra)
        val chooserIntent = Intent.createChooser(intent, getString(R.string.mail))
        startActivity(chooserIntent)
    }

    // Техподдержка
    private fun openSupport(strEmail: String?) {
        val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf(strEmail))
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
        email.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_thank_you))
        val chooserIntent = Intent.createChooser(email, getString(R.string.mail))
        startActivity(chooserIntent)
    }

    // Пользовательское соглашение
    private fun openTerms (uriString: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW , Uri.parse(uriString))
        startActivity(browserIntent)
    }

}