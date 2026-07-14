package com.example.theolaforgeeval.player.ui.screen.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.player.model.Playlist
import com.example.theolaforgeeval.player.repository.PlaylistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val repository: PlaylistRepository,
) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> =
        repository.observePlaylists().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun create(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.create(trimmed) }
    }

    fun delete(id: Int) {
        viewModelScope.launch { repository.delete(id) }
    }
}
