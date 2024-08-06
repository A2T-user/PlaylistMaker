package com.a2t.myapplication.mediateca.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.a2t.myapplication.mediateca.data.db.dao.TrackDao
import com.a2t.myapplication.mediateca.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao
}
