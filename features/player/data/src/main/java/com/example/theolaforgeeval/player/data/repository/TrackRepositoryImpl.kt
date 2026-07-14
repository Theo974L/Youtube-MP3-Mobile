package com.example.theolaforgeeval.player.data.repository

import com.example.theolaforgeeval.player.data.local.dao.TrackDao
import com.example.theolaforgeeval.player.model.Track
import com.example.theolaforgeeval.player.model.TrackEntity
import com.example.theolaforgeeval.player.model.toDomain
import com.example.theolaforgeeval.player.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackRepositoryImpl(
    private val dao: TrackDao
) : TrackRepository {

    override fun getTracks(): Flow<List<Track>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun getTotalBytes(): Flow<Long> = dao.observeTotalBytes()

    override suspend fun existsByYoutubeId(youtubeId: String): Boolean =
        dao.findByYoutubeId(youtubeId) != null

    override suspend fun insert(track: TrackEntity) {
        dao.insert(track)
    }

    override suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
}
