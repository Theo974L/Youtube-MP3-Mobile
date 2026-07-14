package com.example.theolaforgeeval.navhost

/**
 * Répertorie les routes de navigation de l'app.
 *
 * @see Library écran d'accueil : bibliothèque des morceaux téléchargés
 * @see Search écran d'ajout : coller un lien YouTube + télécharger
 */
sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Search : Screen("search")
    object Playlists : Screen("playlists")
    object Player : Screen("player") // lecteur plein écran

    object PlaylistDetail : Screen("playlist/{id}") {
        fun createRoute(id: Int) = "playlist/$id"
    }
}
