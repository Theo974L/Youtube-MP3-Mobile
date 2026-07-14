package com.example.theolaforgeeval.player.repository

import com.example.theolaforgeeval.player.model.Playlist
import com.example.theolaforgeeval.player.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun observePlaylists(): Flow<List<Playlist>>
    fun observeName(id: Int): Flow<String?>
    fun observeTracks(playlistId: Int): Flow<List<Track>>

    /** Crée une playlist et renvoie son id. */
    suspend fun create(name: String): Int
    suspend fun rename(id: Int, name: String)
    suspend fun delete(id: Int)
    suspend fun addTrack(playlistId: Int, trackId: Int)
    suspend fun removeTrack(playlistId: Int, trackId: Int)
}
