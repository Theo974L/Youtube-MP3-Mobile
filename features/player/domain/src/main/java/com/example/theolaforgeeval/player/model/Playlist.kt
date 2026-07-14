package com.example.theolaforgeeval.player.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Une playlist locale. */
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
)

/** Liaison many-to-many playlist <-> morceau. */
@Entity(
    tableName = "playlist_track",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("trackId")],
)
data class PlaylistTrackCrossRef(
    val playlistId: Int,
    val trackId: Int,
    val addedAt: Long = System.currentTimeMillis(),
)

/** Modèle métier exposé à l'UI (nom + nombre de morceaux). */
data class Playlist(
    val id: Int,
    val name: String,
    val trackCount: Int,
)
