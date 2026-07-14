package com.example.theolaforgeeval.player.data.repository

import com.example.theolaforgeeval.player.data.local.dao.PlaylistDao
import com.example.theolaforgeeval.player.model.Playlist
import com.example.theolaforgeeval.player.model.PlaylistEntity
import com.example.theolaforgeeval.player.model.PlaylistTrackCrossRef
import com.example.theolaforgeeval.player.model.Track
import com.example.theolaforgeeval.player.model.toDomain
import com.example.theolaforgeeval.player.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val dao: PlaylistDao,
) : PlaylistRepository {

    override fun observePlaylists(): Flow<List<Playlist>> =
        dao.observePlaylists().map { list ->
            list.map { Playlist(id = it.id, name = it.name, trackCount = it.trackCount) }
        }

    override fun observeName(id: Int): Flow<String?> = dao.observeName(id)

    override fun observeTracks(playlistId: Int): Flow<List<Track>> =
        dao.observeTracksOfPlaylist(playlistId).map { list -> list.map { it.toDomain() } }

    override suspend fun create(name: String): Int =
        dao.insertPlaylist(PlaylistEntity(name = name)).toInt()

    override suspend fun rename(id: Int, name: String) = dao.rename(id, name)

    override suspend fun delete(id: Int) = dao.deletePlaylist(id)

    override suspend fun addTrack(playlistId: Int, trackId: Int) =
        dao.addTrack(PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId))

    override suspend fun removeTrack(playlistId: Int, trackId: Int) =
        dao.removeTrack(playlistId, trackId)
}
