package com.example.theolaforgeeval.player.data.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.theolaforgeeval.player.model.Track
import com.example.theolaforgeeval.player.playback.PlaybackController
import com.example.theolaforgeeval.player.playback.PlaybackState
import com.example.theolaforgeeval.player.playback.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

/**
 * Implémentation Media3 : se connecte au PlaybackService via un MediaController
 * et traduit l'état du player en [PlaybackState] pour l'UI. Un ticker met à jour
 * la position (~2/s) pour la seekbar.
 */
class Media3PlaybackController(
    private val context: Context,
) : PlaybackController {

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var controller: MediaController? = null
    private var pendingAction: (() -> Unit)? = null
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    init {
        connect()
    }

    private fun connect() {
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener(
            {
                val c = future.get()
                c.addListener(playerListener)
                controller = c
                updateState(c)
                pendingAction?.invoke()
                pendingAction = null
                startPositionTicker()
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) = updateState(player)
    }

    private fun startPositionTicker() {
        scope.launch {
            while (isActive) {
                controller?.let { updateState(it) }
                delay(500)
            }
        }
    }

    private fun updateState(player: Player) {
        val meta = player.currentMediaItem?.mediaMetadata
        _state.value = PlaybackState(
            hasMedia = player.currentMediaItem != null,
            isPlaying = player.isPlaying,
            title = meta?.title?.toString(),
            artist = meta?.artist?.toString(),
            artworkUrl = meta?.artworkUri?.toString(),
            positionMs = player.currentPosition.coerceAtLeast(0),
            durationMs = player.duration.takeIf { it > 0 } ?: 0L,
            shuffle = player.shuffleModeEnabled,
            repeatMode = when (player.repeatMode) {
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                else -> RepeatMode.OFF
            },
        )
    }

    override fun play(tracks: List<Track>, startIndex: Int) {
        val action = {
            controller?.run {
                setMediaItems(tracks.map { it.toMediaItem() }, startIndex, 0L)
                prepare()
                play()
            }
            Unit
        }
        if (controller != null) action() else pendingAction = action
    }

    override fun togglePlayPause() {
        controller?.let { if (it.isPlaying) it.pause() else it.play() }
    }

    override fun next() {
        controller?.seekToNextMediaItem()
    }

    override fun previous() {
        controller?.seekToPreviousMediaItem()
    }

    override fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    override fun toggleShuffle() {
        controller?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled }
    }

    override fun cycleRepeat() {
        controller?.let {
            it.repeatMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }

    private fun Track.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(youtubeId)
            .setUri(Uri.fromFile(File(filePath)))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setArtworkUri(thumbnailUrl?.let(Uri::parse))
                    .build(),
            )
            .build()
}
