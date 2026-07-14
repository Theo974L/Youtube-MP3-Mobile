package com.example.theolaforgeeval.player.data.remote

import com.example.theolaforgeeval.player.extractor.AudioExtractor
import com.example.theolaforgeeval.player.model.ExtractedAudio
import com.example.theolaforgeeval.player.model.ExtractionException
import com.example.theolaforgeeval.player.model.SearchResult
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Extraction + recherche via yt-dlp.
 * - extract(url) : métadonnées d'un lien précis (`--dump-json`).
 * - search(query) : recherche YouTube (`ytsearch`) en mode flat (rapide).
 */
class YtDlpExtractor : AudioExtractor {

    override suspend fun extract(url: String): ExtractedAudio = withContext(Dispatchers.IO) {
        val info = try {
            YoutubeDL.getInstance().getInfo(url)
        } catch (e: Exception) {
            throw ExtractionException.Unavailable(e)
        }

        val id = info.id ?: throw ExtractionException.InvalidUrl(url)

        ExtractedAudio(
            youtubeId = id,
            title = info.title ?: "Sans titre",
            artist = info.uploader,
            durationSec = info.duration.toLong(),
            thumbnailUrl = info.thumbnail,
            audioStreamUrl = url,
            fileSuffix = "mp3",
            averageBitrate = 0,
        )
    }

    override suspend fun search(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val request = YoutubeDLRequest("ytsearch$SEARCH_LIMIT:$query").apply {
            addOption("--flat-playlist")  // rapide : pas d'extraction complète par vidéo
            addOption("--dump-json")      // un objet JSON par ligne
            addOption("--no-warnings")
        }

        val response = try {
            YoutubeDL.getInstance().execute(request, null, false, null)
        } catch (e: Exception) {
            throw ExtractionException.Unavailable(e)
        }

        response.out
            .lineSequence()
            .map { it.trim() }
            .filter { it.startsWith("{") }
            .mapNotNull { parseSearchLine(it) }
            .toList()
    }

    private fun parseSearchLine(line: String): SearchResult? = try {
        val o = JSONObject(line)
        val id = o.optString("id").ifBlank { return null }
        SearchResult(
            videoUrl = o.optString("url").ifBlank { "https://www.youtube.com/watch?v=$id" },
            youtubeId = id,
            title = o.optString("title").ifBlank { "Sans titre" },
            uploader = o.optString("channel").ifBlank { o.optString("uploader").ifBlank { null } },
            durationSec = o.optDouble("duration", 0.0).toLong(),
            thumbnailUrl = bestThumbnail(o),
        )
    } catch (e: Exception) {
        null
    }

    private fun bestThumbnail(o: JSONObject): String? {
        val arr = o.optJSONArray("thumbnails") ?: return o.optString("thumbnail").ifBlank { null }
        var best: String? = null
        for (i in 0 until arr.length()) {
            val url = arr.optJSONObject(i)?.optString("url")?.ifBlank { null }
            if (url != null) best = url // le dernier est en général la meilleure résolution
        }
        return best ?: o.optString("thumbnail").ifBlank { null }
    }

    companion object {
        private const val SEARCH_LIMIT = 20
    }
}
