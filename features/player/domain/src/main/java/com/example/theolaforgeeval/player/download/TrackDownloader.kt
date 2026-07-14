package com.example.theolaforgeeval.player.download

import kotlinx.coroutines.flow.Flow
import java.util.UUID

/** État d'un téléchargement, exposé à l'UI sans fuiter WorkManager. */
sealed class DownloadStatus {
    data object Idle : DownloadStatus()
    data class Running(val title: String?) : DownloadStatus()
    data class Success(val title: String?, val alreadyExists: Boolean) : DownloadStatus()
    data class Failed(val error: String) : DownloadStatus()
}

/**
 * Abstraction du téléchargement en arrière-plan (implémentée côté data via WorkManager).
 */
interface TrackDownloader {
    /** Lance le téléchargement et renvoie l'id du travail à observer. */
    fun enqueue(url: String): UUID

    /** Flux d'états pour un id donné. */
    fun observeStatus(id: UUID): Flow<DownloadStatus>
}
