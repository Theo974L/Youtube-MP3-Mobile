package com.example.theolaforgeeval.core.extensions

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes



/**
 * Joue un son à partir d'une ressource raw dans le contexte actuel.
 *
 * Cette fonction utilise [MediaPlayer] pour jouer le son et s'assure que
 * la ressource est libérée lorsque la lecture est terminée.
 *
 * Cette extension est détachée de l'UI, ce qui permet de centraliser
 * l'accès aux capacités audio du device.
 *
 * @receiver Context Le contexte depuis lequel le son sera joué.
 * @param soundRes L'identifiant de la ressource audio dans `res/raw` à jouer.
 *                 Doit être annoté avec [RawRes] pour garantir la sécurité des types.
 * @param loop Si vrai, le son sera joué en boucle jusqu'à ce que le [MediaPlayer] soit arrêté.
 */

// EXTENSION DU CONTEXT
fun Context.playSound(@RawRes soundRes: Int, loop: Boolean = false) {
    // Créez un MediaPlayer pour jouer le son
    val mediaPlayer = MediaPlayer.create(this, soundRes)

    mediaPlayer.isLooping = loop

    // Quand la musique se finit, libérez la ressource
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
    mediaPlayer.start()
}