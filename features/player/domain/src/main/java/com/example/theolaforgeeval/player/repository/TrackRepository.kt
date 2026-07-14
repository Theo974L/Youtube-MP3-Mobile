package com.example.theolaforgeeval.player.repository

import com.example.theolaforgeeval.player.model.Track
import com.example.theolaforgeeval.player.model.TrackEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contrat de la bibliothèque de morceaux (source unique de vérité).
 * Implémenté côté data, injecté via Koin.
 */
interface TrackRepository {
    fun getTracks(): Flow<List<Track>>
    fun getTotalBytes(): Flow<Long>
    suspend fun existsByYoutubeId(youtubeId: String): Boolean
    suspend fun insert(track: TrackEntity)
    suspend fun deleteById(id: Int)
}
