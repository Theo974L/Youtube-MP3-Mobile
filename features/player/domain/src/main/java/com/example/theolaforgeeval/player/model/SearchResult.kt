package com.example.theolaforgeeval.player.model

/** Un résultat de recherche YouTube (ou un lien collé résolu). */
data class SearchResult(
    val videoUrl: String,
    val youtubeId: String,
    val title: String,
    val uploader: String?,
    val durationSec: Long,
    val thumbnailUrl: String?,
)
