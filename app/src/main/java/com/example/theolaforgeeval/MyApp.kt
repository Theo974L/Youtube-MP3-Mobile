package com.example.theolaforgeeval

import android.app.Application
import android.util.Log
import com.example.theolaforgeeval.player.api.PlayerModule
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(
                PlayerModule
            )
        }

        // Init yt-dlp + ffmpeg embarqués. Fait en arrière-plan car le premier lancement
        // copie les binaires natifs (peut prendre 1-2 s) — on évite tout blocage de l'UI.
        Thread {
            try {
                YoutubeDL.getInstance().init(this)
                FFmpeg.getInstance().init(this)
                // Met yt-dlp à jour : c'est ce qui garde l'extraction fonctionnelle
                // quand YouTube change (non bloquant, échec silencieux si hors ligne).
                runCatching {
                    YoutubeDL.getInstance()
                        .updateYoutubeDL(this, YoutubeDL.UpdateChannel.STABLE)
                }
            } catch (e: Exception) {
                Log.e("MyApp", "Init yt-dlp échouée", e)
            }
        }.start()
    }
}
