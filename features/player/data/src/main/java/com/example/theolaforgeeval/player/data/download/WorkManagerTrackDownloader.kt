package com.example.theolaforgeeval.player.data.download

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.theolaforgeeval.player.download.DownloadStatus
import com.example.theolaforgeeval.player.download.TrackDownloader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implémentation WorkManager de TrackDownloader.
 * Traduit les WorkInfo (Android) en DownloadStatus (domain) pour l'UI.
 */
class WorkManagerTrackDownloader(
    context: Context
) : TrackDownloader {

    private val workManager = WorkManager.getInstance(context)

    override fun enqueue(url: String): UUID {
        val request = DownloadWorker.request(url)
        // REPLACE : un double-clic remplace le travail précédent (l'id observé reste valide).
        workManager.enqueueUniqueWork("download_$url", ExistingWorkPolicy.REPLACE, request)
        return request.id
    }

    override fun observeStatus(id: UUID): Flow<DownloadStatus> =
        workManager.getWorkInfoByIdFlow(id).map { info ->
            when (info?.state) {
                null, WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> DownloadStatus.Idle
                WorkInfo.State.RUNNING -> DownloadStatus.Running(
                    info.progress.getString(DownloadWorker.KEY_PROGRESS_TITLE)
                )
                WorkInfo.State.SUCCEEDED -> DownloadStatus.Success(
                    title = info.outputData.getString(DownloadWorker.KEY_RESULT_TITLE),
                    alreadyExists = info.outputData.getBoolean(DownloadWorker.KEY_ALREADY_EXISTS, false)
                )
                WorkInfo.State.FAILED -> DownloadStatus.Failed(
                    info.outputData.getString(DownloadWorker.KEY_ERROR) ?: "Téléchargement échoué"
                )
                WorkInfo.State.CANCELLED -> DownloadStatus.Idle
            }
        }
}
