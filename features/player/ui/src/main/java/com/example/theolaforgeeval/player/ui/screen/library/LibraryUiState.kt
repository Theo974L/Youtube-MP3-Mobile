package com.example.theolaforgeeval.player.ui.screen.library

import com.example.theolaforgeeval.player.model.Track

data class LibraryUiState(
    val tracks: List<Track> = emptyList(),
    val totalBytes: Long = 0L,
    val isLoading: Boolean = true,
) {
    val count: Int get() = tracks.size
    val isEmpty: Boolean get() = !isLoading && tracks.isEmpty()
}
