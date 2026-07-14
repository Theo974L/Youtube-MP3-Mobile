// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// ============================================================================
// CONTOURNEMENT TEMPORAIRE — verrou de fichier Windows sur build/*/classes.jar
// ----------------------------------------------------------------------------
// Un process fantôme (daemon Java d'un build planté) garde l'ancien classes.jar
// ouvert : AGP échoue en voulant le SUPPRIMER avant de le réécrire.
// Parade : chaque invocation Gradle écrit dans un dossier de build FLAMBANT NEUF
// (horodaté), sous AppData\Local. Il n'y a donc jamais d'ancien fichier à
// supprimer -> plus de conflit, quel que soit le process qui tient le vieux.
//
// >>> C'est une rustine. Une fois la vraie cause réglée (reboot pour tuer le
//     process fantôme + exclusion antivirus), REMETS un dossier fixe ou supprime
//     ce bloc pour retrouver des builds incrémentaux normaux.
//     Pense aussi à vider C:\Users\<toi>\AppData\Local\yt-builds de temps en temps.
// ============================================================================
run {
    val buildHome = System.getenv("LOCALAPPDATA")
        ?: System.getProperty("java.io.tmpdir")
    // Dossier de build NEUF à chaque invocation : rien à supprimer -> le verrou d'un
    // process externe (Android Studio / antivirus) sur l'ancien classes.jar n'a plus d'effet.
    // >>> Comme le chemin change, NE PAS utiliser le bouton Run d'AS (il chercherait l'ancien
    //     dossier). Builder + installer en UNE commande CLI : `gradlew installDebug`.
    val buildRoot = File(File(buildHome, "yt-builds"), "run-${System.currentTimeMillis()}")

    rootProject.layout.buildDirectory.set(File(buildRoot, "root"))
    subprojects {
        val key = project.path.trim(':').replace(':', '-')
        layout.buildDirectory.set(File(buildRoot, key))
    }
}