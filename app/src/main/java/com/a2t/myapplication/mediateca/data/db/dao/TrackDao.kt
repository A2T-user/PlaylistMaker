package com.a2t.myapplication.mediateca.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.a2t.myapplication.mediateca.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favorite_table WHERE trackId = :trackId")
    fun deleteTrackById (trackId:Int)

    @Query("SELECT * FROM favorite_table ORDER BY id DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorite_table")
    fun getTracksId(): List<Int>
}