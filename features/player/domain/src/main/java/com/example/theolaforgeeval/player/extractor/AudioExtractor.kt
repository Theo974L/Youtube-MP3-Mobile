package com.example.theolaforgeeval.player.extractor

import com.example.theolaforgeeval.player.model.ExtractedAudio
import com.example.theolaforgeeval.player.model.SearchResult

/**
 * Abstraction de l'extraction (implémentée côté data par yt-dlp).
 * Permet à l'UI de ne dépendre que du domain.
 */
interface AudioExtractor {
    /** Métadonnées d'un lien précis (aperçu / lien collé). */
    suspend fun extract(url: String): ExtractedAudio

    /** Recherche YouTube par mots-clés -> liste de résultats. */
    suspend fun search(query: String): List<SearchResult>
}
