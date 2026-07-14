package com.example.theolaforgeeval.player.ui.screen.search

import com.example.theolaforgeeval.player.model.SearchResult

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val downloadingUrls: Set<String> = emptySet(),
    val downloadedUrls: Set<String> = emptySet(),
    val message: String? = null,
    val error: String? = null,
) {
    val noResults: Boolean get() = hasSearched && !isSearching && results.isEmpty()
}
