package com.a2t.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val arrow = findViewById<ImageView>(R.id.iv_arrow)
        arrow.setOnClickListener {
            finish()
        }
    }
}