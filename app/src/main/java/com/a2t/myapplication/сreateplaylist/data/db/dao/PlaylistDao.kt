package com.a2t.myapplication.сreateplaylist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a2t.myapplication.сreateplaylist.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // Добавление плейлиста
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(track: PlaylistEntity): Long

    // Обновление плейлиста
    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun updatePlaylist(playlist: PlaylistEntity): Int

    // Возвращает список плейлистов
    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>
}