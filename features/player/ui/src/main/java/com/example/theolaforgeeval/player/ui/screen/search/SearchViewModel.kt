package com.example.theolaforgeeval.player.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.player.download.DownloadStatus
import com.example.theolaforgeeval.player.download.TrackDownloader
import com.example.theolaforgeeval.player.extractor.AudioExtractor
import com.example.theolaforgeeval.player.model.ExtractedAudio
import com.example.theolaforgeeval.player.model.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Écran "Ajouter" : recherche par mots-clés OU lien collé, puis téléchargement
 * du résultat choisi. Ne dépend que d'abstractions domain (Koin).
 */
class SearchViewModel(
    private val extractor: AudioExtractor,
    private val downloader: TrackDownloader,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun clearMessages() {
        _state.update { it.copy(message = null, error = null) }
    }

    fun search() {
        val query = _state.value.query.trim()
        if (query.isEmpty()) return
        _state.update {
            it.copy(isSearching = true, hasSearched = true, results = emptyList(), error = null, message = null)
        }
        viewModelScope.launch {
            runCatching {
                if (query.looksLikeUrl()) listOf(extractor.extract(query).toResult(query))
                else extractor.search(query)
            }.onSuccess { results ->
                _state.update { it.copy(isSearching = false, results = results) }
            }.onFailure { e ->
                _state.update { it.copy(isSearching = false, error = e.message ?: "Recherche échouée") }
            }
        }
    }

    fun download(result: SearchResult) {
        val url = result.videoUrl
        val current = _state.value
        if (url in current.downloadingUrls || url in current.downloadedUrls) return

        val id = downloader.enqueue(url)
        _state.update { it.copy(downloadingUrls = it.downloadingUrls + url) }

        viewModelScope.launch {
            downloader.observeStatus(id).collect { status ->
                when (status) {
                    is DownloadStatus.Success -> _state.update {
                        it.copy(
                            downloadingUrls = it.downloadingUrls - url,
                            downloadedUrls = it.downloadedUrls + url,
                            message = "« ${status.title ?: result.title} » ajouté à la bibliothèque",
                        )
                    }
                    is DownloadStatus.Failed -> _state.update {
                        it.copy(downloadingUrls = it.downloadingUrls - url, error = status.error)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun String.looksLikeUrl(): Boolean =
        startsWith("http", ignoreCase = true) || contains("youtube.com") || contains("youtu.be")

    private fun ExtractedAudio.toResult(url: String) = SearchResult(
        videoUrl = url,
        youtubeId = youtubeId,
        title = title,
        uploader = artist,
        durationSec = durationSec,
        thumbnailUrl = thumbnailUrl,
    )
}
