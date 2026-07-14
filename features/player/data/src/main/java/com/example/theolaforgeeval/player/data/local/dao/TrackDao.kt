package com.example.theolaforgeeval.player.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.theolaforgeeval.player.model.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE youtubeId = :youtubeId LIMIT 1")
    suspend fun findByYoutubeId(youtubeId: String): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity): Long

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COALESCE(SUM(fileSizeBytes), 0) FROM tracks")
    fun observeTotalBytes(): Flow<Long>
}
