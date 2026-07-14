package com.example.theolaforgeeval.player.ui.screen.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.player.model.Track
import com.example.theolaforgeeval.player.playback.PlaybackController
import com.example.theolaforgeeval.player.repository.PlaylistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistId: Int,
    private val repository: PlaylistRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    val name: StateFlow<String?> = repository.observeName(playlistId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null,
    )

    val tracks: StateFlow<List<Track>> = repository.observeTracks(playlistId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList(),
    )

    fun play(index: Int) {
        playbackController.play(tracks.value, index)
    }

    fun playAll() {
        val list = tracks.value
        if (list.isNotEmpty()) playbackController.play(list, 0)
    }

    fun remove(track: Track) {
        viewModelScope.launch { repository.removeTrack(playlistId, track.id) }
    }

    fun deletePlaylist() {
        viewModelScope.launch { repository.delete(playlistId) }
    }
}
