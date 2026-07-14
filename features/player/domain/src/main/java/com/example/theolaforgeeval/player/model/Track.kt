package com.example.theolaforgeeval.player.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room : un morceau téléchargé, persisté en base.
 * (On garde le même style que CategoryEntity : entité dans le module domain.)
 */
@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val youtubeId: String,
    val title: String,
    val artist: String?,
    val durationSec: Long,
    val filePath: String,
    val fileSizeBytes: Long,
    val thumbnailUrl: String?,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Modèle métier exposé à l'UI (découplé de Room).
 */
data class Track(
    val id: Int,
    val youtubeId: String,
    val title: String,
    val artist: String?,
    val durationSec: Long,
    val filePath: String,
    val fileSizeBytes: Long,
    val thumbnailUrl: String?
)

fun TrackEntity.toDomain() = Track(
    id = id,
    youtubeId = youtubeId,
    title = title,
    artist = artist,
    durationSec = durationSec,
    filePath = filePath,
    fileSizeBytes = fileSizeBytes,
    thumbnailUrl = thumbnailUrl
)
