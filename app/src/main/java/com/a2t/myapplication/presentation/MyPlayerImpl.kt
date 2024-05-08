package com.a2t.myapplication.presentation

import android.media.MediaPlayer
import com.a2t.myapplication.domain.api.MyPlayer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class MyPlayerImpl : MediaPlayer(),MyPlayer {
    override fun setDataSource(url: String?) {
        super.setDataSource(url)
    }

    override fun preparePlayer() {
        val executor = Executors.newCachedThreadPool()
        executor.execute { super.prepare()}
    }

    override fun currentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this.currentPosition)
    }
    override fun setOnPreparedListener(listener: OnPreparedListener) {
        super.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: OnCompletionListener) {
        super.setOnCompletionListener(listener)
    }
}