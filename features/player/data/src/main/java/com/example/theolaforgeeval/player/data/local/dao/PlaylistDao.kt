package com.example.theolaforgeeval.player.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.theolaforgeeval.player.model.PlaylistEntity
import com.example.theolaforgeeval.player.model.PlaylistTrackCrossRef
import com.example.theolaforgeeval.player.model.TrackEntity
import kotlinx.coroutines.flow.Flow

/** Projection : playlist + nombre de morceaux. */
data class PlaylistWithCount(
    val id: Int,
    val name: String,
    val trackCount: Int,
)

@Dao
interface PlaylistDao {

    @Query(
        """
        SELECT p.id AS id, p.name AS name, COUNT(pt.trackId) AS trackCount
        FROM playlists p
        LEFT JOIN playlist_track pt ON pt.playlistId = p.id
        GROUP BY p.id
        ORDER BY p.createdAt DESC
        """,
    )
    fun observePlaylists(): Flow<List<PlaylistWithCount>>

    @Query("SELECT name FROM playlists WHERE id = :id")
    fun observeName(id: Int): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :name WHERE id = :id")
    suspend fun rename(id: Int, name: String)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(ref: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_track WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrack(playlistId: Int, trackId: Int)

    @Query(
        """
        SELECT t.* FROM tracks t
        INNER JOIN playlist_track pt ON pt.trackId = t.id
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.addedAt ASC
        """,
    )
    fun observeTracksOfPlaylist(playlistId: Int): Flow<List<TrackEntity>>
}
