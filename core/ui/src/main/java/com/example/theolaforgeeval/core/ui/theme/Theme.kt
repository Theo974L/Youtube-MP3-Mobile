package com.example.theolaforgeeval.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


/**
 *
 *
 * @see LightColorScheme couleur pour le theme clair
 * @see DarkColorScheme couleur pour le theme sombre
 *
 * @see TheoLaforgeEvalTheme c'est la logique pour utiliser soit le theme sombre soit le theme clair
 *
 */

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.Secondary,
    tertiary = AppColors.Tertiary,

    background = AppColors.LightBackground,
    surface = AppColors.LightSurface,
    surfaceVariant = AppColors.LightSurfaceVariant,

    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = AppColors.OnLightBackground,
    onSurface = AppColors.OnLightSurface,
    onSurfaceVariant = AppColors.OnLightSurfaceVariant,

    error = AppColors.Error
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.Secondary,
    tertiary = AppColors.Tertiary,

    background = AppColors.Background,
    surface = AppColors.Surface,
    surfaceVariant = AppColors.SurfaceVariant,

    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = AppColors.OnBackground,
    onSurface = AppColors.OnSurface,
    onSurfaceVariant = AppColors.OnSurfaceVariant,

    error = AppColors.Error
)


@Composable
fun TheoLaforgeEvalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // false = on force TA palette (clair/sombre définie ci-dessus).
    // true = couleurs dynamiques du fond d'écran (Material You, Android 12+).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = remember(darkTheme, dynamicColor) {
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

                try {
                    if (darkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                } catch (e: Exception) {
                    if (darkTheme) DarkColorScheme else LightColorScheme
                }
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}