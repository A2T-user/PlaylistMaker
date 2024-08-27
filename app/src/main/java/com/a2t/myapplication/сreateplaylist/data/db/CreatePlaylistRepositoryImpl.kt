package com.a2t.myapplication.сreateplaylist.data.db

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.net.toUri
import com.a2t.myapplication.mediateca.data.db.AppDatabase
import com.a2t.myapplication.сreateplaylist.data.db.entity.PlaylistEntity
import com.a2t.myapplication.сreateplaylist.domain.db.CreatePlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CreatePlaylistRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
): CreatePlaylistRepository {

    // Добавление плейлиста
    override fun addNewPlaylist(playlist: PlaylistEntity): Flow<Long> = flow {
        val id = appDatabase.getPlaylistDao().insertPlaylist(playlist)
        emit(id)
    }

    // Обновление плейлиста
    override fun updatePlaylist(playlist: PlaylistEntity): Flow<Int> = flow {
        val count = appDatabase.getPlaylistDao().updatePlaylist(playlist)
        emit(count)
    }

    // Копирует обложку плей листа в хранилище приложения
    override fun saveImageToPrivateStorage(uri: String): String {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл с тем же имненим, что и исходный, внутри каталога
        val  zdt = ZonedDateTime.now( )
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd_HH-mm-ss")
        val fileName = formatter.format(zdt)  + ".jpg"
        val file = File(filePath, fileName)

        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri.toUri())
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri().toString()
    }
}