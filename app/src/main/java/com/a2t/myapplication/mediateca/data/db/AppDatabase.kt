package com.a2t.myapplication.mediateca.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.a2t.myapplication.mediateca.data.db.dao.TrackDao
import com.a2t.myapplication.mediateca.data.db.entity.TrackEntity
import com.a2t.myapplication.player.data.db.dao.TracksFromPlaylistsDao
import com.a2t.myapplication.player.data.db.entity.TrackFromPlaylistsEntity
import com.a2t.myapplication.сreateplaylist.data.db.dao.PlaylistDao
import com.a2t.myapplication.сreateplaylist.data.db.entity.PlaylistEntity

@Database(version = 6, entities = [TrackEntity::class, PlaylistEntity::class, TrackFromPlaylistsEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao

    abstract fun getPlaylistDao(): PlaylistDao

    abstract fun getTracksFromPlaylistsDao(): TracksFromPlaylistsDao

}
