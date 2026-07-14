package com.example.theolaforgeeval.player.data.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.theolaforgeeval.player.extractor.AudioExtractor
import com.example.theolaforgeeval.player.model.ExtractionException
import com.example.theolaforgeeval.player.model.TrackEntity
import com.example.theolaforgeeval.player.repository.TrackRepository
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Télécharge l'audio d'une vidéo YouTube et le convertit en MP3, via yt-dlp.
 * CoroutineWorker => survit à la fermeture de l'app. Dépendances via Koin (KoinComponent).
 */
class DownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val extractor: AudioExtractor by inject()
    private val repository: TrackRepository by inject()

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_URL)
            ?: return Result.failure(errorData("Aucune URL fournie"))

        return try {
            // 1) Métadonnées (titre, id, durée, miniature) via yt-dlp --dump-json.
            val audio = extractor.extract(url)

            if (repository.existsByYoutubeId(audio.youtubeId)) {
                return Result.success(
                    workDataOf(KEY_RESULT_TITLE to audio.title, KEY_ALREADY_EXISTS to true)
                )
            }

            setProgress(workDataOf(KEY_PROGRESS_TITLE to audio.title))

            val musicDir = File(applicationContext.filesDir, "music").apply { mkdirs() }
            val mp3File = File(musicDir, "${audio.youtubeId}.mp3")

            // 2) Téléchargement + conversion MP3 en une seule commande yt-dlp.
            val exitCode = withContext(Dispatchers.IO) {
                val request = YoutubeDLRequest(url).apply {
                    addOption("-x")                        // extract audio
                    addOption("--audio-format", "mp3")
                    addOption("--audio-quality", "0")      // meilleure qualité
                    addOption("--embed-thumbnail")         // pochette dans le MP3 (ffmpeg)
                    addOption("--add-metadata")            // titre/artiste dans les tags
                    addOption("--no-playlist")             // un seul morceau
                    addOption("--no-mtime")
                    addOption("-o", "${musicDir.absolutePath}/%(id)s.%(ext)s")
                }
                // Signature : execute(request, processId, redirectErrorStream, callback)
                // 4 args positionnels -> lève toute ambiguïté de surcharge.
                YoutubeDL.getInstance()
                    .execute(request, audio.youtubeId, false, null)
                    .exitCode
            }

            if (exitCode != 0 || !mp3File.exists() || mp3File.length() <= 0L) {
                mp3File.delete()
                return Result.failure(errorData("Téléchargement / conversion MP3 échoué"))
            }

            // 3) Enregistrement en base.
            repository.insert(
                TrackEntity(
                    youtubeId = audio.youtubeId,
                    title = audio.title,
                    artist = audio.artist,
                    durationSec = audio.durationSec,
                    filePath = mp3File.absolutePath,
                    fileSizeBytes = mp3File.length(),
                    thumbnailUrl = audio.thumbnailUrl
                )
            )

            Result.success(workDataOf(KEY_RESULT_TITLE to audio.title))
        } catch (e: ExtractionException) {
            Result.failure(errorData(e.message ?: "Extraction échouée"))
        } catch (e: Exception) {
            // Erreur transitoire (réseau, yt-dlp) => WorkManager réessaie.
            if (runAttemptCount < MAX_ATTEMPTS) Result.retry()
            else Result.failure(errorData(e.message ?: "Erreur inconnue"))
        }
    }

    private fun errorData(message: String): Data = workDataOf(KEY_ERROR to message)

    companion object {
        const val KEY_URL = "url"
        const val KEY_ERROR = "error"
        const val KEY_RESULT_TITLE = "result_title"
        const val KEY_PROGRESS_TITLE = "progress_title"
        const val KEY_ALREADY_EXISTS = "already_exists"

        private const val MAX_ATTEMPTS = 3

        fun inputData(url: String): Data = workDataOf(KEY_URL to url)

        fun request(url: String) =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(inputData(url))
                .build()
    }
}
