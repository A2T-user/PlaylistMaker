package com.a2t.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrow = findViewById<ImageView>(R.id.iv_arrow)
        val ll_send = findViewById<LinearLayout>(R.id.ll_send)
        val ll_support = findViewById<LinearLayout>(R.id.ll_support)
        val ll_user_agreement = findViewById<LinearLayout>(R.id.ll_user_agreement)

        // Закрытие SettingsActivity по стрелке
        arrow.setOnClickListener {
            finish()
        }

        // Поделиться приложением
        ll_send.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_android))
            val chooserIntent = Intent.createChooser(intent, getString(R.string.mail))
            startActivity(chooserIntent)
        }

        // Техподдержка
        ll_support.setOnClickListener {
            val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(getString(R.string.e_mail)))
            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
            email.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_thank_you))
            startActivity(Intent.createChooser(email, getString(R.string.mail)))
        }

        // Пользовательское соглашение
        ll_user_agreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer)))
            startActivity(browserIntent)
        }
    }
}