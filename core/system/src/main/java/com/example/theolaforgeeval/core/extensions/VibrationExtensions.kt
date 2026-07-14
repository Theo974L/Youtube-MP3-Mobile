@file:Suppress("MissingPermission")

package com.example.theolaforgeeval.core.extensions

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager


/**
 * Fait vibrer l'appareil depuis le contexte courant.
 *
 * Cette fonction centralise l'accès au service de vibration du device
 * et gère automatiquement les différences entre les versions Android.
 *
 * @receiver Context Le contexte depuis lequel la vibration est déclenchée.
 * @param duration Durée de la vibration en millisecondes. Valeur par défaut : 100 ms.
 * @param amplitude Intensité de la vibration (0-255). Valeur par défaut : -1 (valeur système par défaut).
 *
 * @note Pour Android O et supérieur, utilise [VibrationEffect] pour un contrôle précis.
 *       Pour les versions antérieures, utilise la méthode dépréciée `vibrator.vibrate(duration)`.
 */

// EXTENSION DU CONTEXT
fun Context.vibrate(duration: Long = 100L, amplitude: Int = -1) {

    // Récupère le service Vibrator ou VibratorManager selon la version d'Android
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // Applique la vibration selon la version Android
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Vibration avec des parametres
        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
    } else {
        @Suppress("DEPRECATION")
        // Vibration simple parce que ancienne version
        vibrator.vibrate(duration)
    }
}
