package com.example.theolaforgeeval.core.ui.utils

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


/**
 * Sert a activer le plein ecran dans l'application
 * Concretement on enleve ici la tool bar native du téléphone
 *
 * @see Activity.enableFullScreenMode c'est une extension de l'activity
 *
 *
 */

fun Activity.enableFullScreenMode() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}