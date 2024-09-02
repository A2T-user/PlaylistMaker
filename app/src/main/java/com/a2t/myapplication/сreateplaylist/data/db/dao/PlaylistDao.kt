package com.a2t.myapplication.сreateplaylist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a2t.myapplication.player.data.db.entity.TrackFromPlaylistsEntity
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

    // Возвращает список всех плейлистов
    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    // Возвращает плейлист по id
    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Long): PlaylistEntity

    // Возвращает список всех треков из таблицы 'tracks_from_playlists_table'
    @Query("SELECT * FROM tracks_from_playlists_table ORDER BY updateTime DESC")
    fun getListOfAllPlaylistTracks(): Flow<List<TrackFromPlaylistsEntity>>

    // Удаление трека по trackId из таблицы 'tracks_from_playlists_table'
    @Query("DELETE FROM tracks_from_playlists_table WHERE trackId = :trackId")
    fun deleteTrackById (trackId:Int)

    // Удаление плейлиста по id из таблицы 'playlist_table'
    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    fun deletePlaylistById (playlistId:Long)
}