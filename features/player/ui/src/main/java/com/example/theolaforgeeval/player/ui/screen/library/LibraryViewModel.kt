package com.example.theolaforgeeval.player.ui.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.player.model.Playlist
import com.example.theolaforgeeval.player.model.Track
import com.example.theolaforgeeval.player.playback.PlaybackController
import com.example.theolaforgeeval.player.repository.PlaylistRepository
import com.example.theolaforgeeval.player.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LibraryViewModel(
    private val repository: TrackRepository,
    private val playbackController: PlaybackController,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    val state: StateFlow<LibraryUiState> =
        combine(repository.getTracks(), repository.getTotalBytes()) { tracks, bytes ->
            LibraryUiState(tracks = tracks, totalBytes = bytes, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState(isLoading = true),
        )

    val playlists: StateFlow<List<Playlist>> =
        playlistRepository.observePlaylists().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun addToPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch { playlistRepository.addTrack(playlistId, trackId) }
    }

    fun createPlaylistAndAdd(name: String, trackId: Int) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val id = playlistRepository.create(trimmed)
            playlistRepository.addTrack(id, trackId)
        }
    }

    /** Lance la lecture de toute la bibliothèque à partir du morceau tapé. */
    fun play(index: Int) {
        playbackController.play(state.value.tracks, index)
    }

    /** Supprime le fichier MP3 puis l'entrée en base. */
    fun delete(track: Track) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runCatching { File(track.filePath).delete() }
            }
            repository.deleteById(track.id)
        }
    }
}
