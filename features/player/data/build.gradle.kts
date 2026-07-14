@file:Suppress("DEPRECATION")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.theolaforgeeval.features.player.data"
    compileSdk = 36

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":features:player:domain"))

    implementation(libs.bundles.common.core)
    implementation(libs.kotlinx.coroutines.android) // Dispatchers/withContext/Flow
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // WorkManager (téléchargement fiable en arrière-plan)
    implementation(libs.androidx.work.runtime.ktx)

    // yt-dlp embarqué : extraction YouTube + conversion MP3 en une passe
    implementation(libs.youtubedl.android)
    implementation(libs.youtubedl.ffmpeg)

    // Media3 : lecture audio en fond + notification média
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
}
