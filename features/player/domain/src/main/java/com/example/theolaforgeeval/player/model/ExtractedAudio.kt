package com.example.theolaforgeeval.player.model

/**
 * Résultat d'une extraction : métadonnées + meilleur flux audio d'une vidéo YouTube.
 * L'UI s'en sert pour l'aperçu, le Worker pour télécharger.
 */
data class ExtractedAudio(
    val youtubeId: String,
    val title: String,
    val artist: String?,
    val durationSec: Long,
    val thumbnailUrl: String?,
    /** URL directe (temporaire) du flux audio à télécharger. */
    val audioStreamUrl: String,
    /** Suffixe conteneur ("m4a", "webm"...) pour l'extension de fichier. */
    val fileSuffix: String,
    val averageBitrate: Int
)

/** Erreurs métier lisibles remontées jusqu'à l'UI. */
sealed class ExtractionException(message: String, cause: Throwable? = null) :
    Exception(message, cause) {
    class InvalidUrl(url: String) : ExtractionException("Lien invalide : $url")
    class NoAudioStream : ExtractionException("Aucun flux audio disponible pour cette vidéo")
    class Unavailable(cause: Throwable) :
        ExtractionException("Vidéo indisponible ou extraction échouée", cause)
}
