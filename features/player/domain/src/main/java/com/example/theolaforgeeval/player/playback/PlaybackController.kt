package com.example.theolaforgeeval.player.playback

import com.example.theolaforgeeval.player.model.Track
import kotlinx.coroutines.flow.StateFlow

enum class RepeatMode { OFF, ALL, ONE }

/** État courant de lecture, exposé à l'UI (mini-lecteur + lecteur plein écran). */
data class PlaybackState(
    val hasMedia: Boolean = false,
    val isPlaying: Boolean = false,
    val title: String? = null,
    val artist: String? = null,
    val artworkUrl: String? = null,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
)

/**
 * Contrôle de la lecture audio (implémenté côté data via Media3/ExoPlayer).
 * L'UI ne dépend que de cette abstraction.
 */
interface PlaybackController {
    val state: StateFlow<PlaybackState>

    /** Charge [tracks] comme file de lecture et démarre à [startIndex]. */
    fun play(tracks: List<Track>, startIndex: Int)
    fun togglePlayPause()
    fun next()
    fun previous()
    fun seekTo(positionMs: Long)
    fun toggleShuffle()
    fun cycleRepeat()
}
