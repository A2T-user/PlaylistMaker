package com.a2t.myapplication.mediateca.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.a2t.myapplication.mediateca.data.db.dao.TrackDao
import com.a2t.myapplication.mediateca.data.db.entity.TrackEntity
import com.a2t.myapplication.сreateplaylist.data.db.dao.PlaylistDao
import com.a2t.myapplication.сreateplaylist.data.db.entity.PlaylistEntity

@Database(version = 5, entities = [TrackEntity::class, PlaylistEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao

    abstract fun getPlaylistDao(): PlaylistDao

}
