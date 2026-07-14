package com.example.theolaforgeeval.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 *
 * @see AppColors Couleur de l'application
 *
 * Toutes les couleurs ne sont pas utilisées mais elles sont en prévisions
 */

object AppColors {

    // ===== Communs aux deux thèmes =====
    // PRIMARY (bleu moderne profond)
    val Primary = Color(0xFF4C8DFF)
    val PrimaryDark = Color(0xFF2F6BFF)

    // SECONDARY (vert moderne doux)
    val Secondary = Color(0xFF22C55E)

    // ACCENT (orange contrôlé)
    val Tertiary = Color(0xFFF59E0B)

    // STATES
    val Error = Color(0xFFEF4444)
    val Success = Color(0xFF22C55E)

    // ===== THEME SOMBRE =====
    val Background = Color(0xFF0B1220)      // dark soft, pas noir
    val Surface = Color(0xFF111A2E)
    val SurfaceVariant = Color(0xFF16213A)
    val OnBackground = Color(0xFFE6EAF2)
    val OnSurface = Color(0xFFE6EAF2)
    val OnSurfaceVariant = Color(0xFF9AA4B2)
    val Border = Color(0x1FFFFFFF)

    // ===== THEME CLAIR =====
    val LightBackground = Color(0xFFF7F9FC) // presque blanc
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceVariant = Color(0xFFE9EEF5)
    val OnLightBackground = Color(0xFF0B1220) // texte sombre
    val OnLightSurface = Color(0xFF0B1220)
    val OnLightSurfaceVariant = Color(0xFF5A6472)
    val LightBorder = Color(0x1F000000)
}