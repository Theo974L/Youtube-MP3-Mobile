@file:Suppress("DEPRECATION")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.theolaforgeeval"
    compileSdk = 36

    defaultConfig {
        // Identité de l'app installée (distincte du template pour cohabiter sur le tél).
        // Le namespace (package du code) reste inchangé : pas besoin de renommer le code.
        applicationId = "com.laforge.ytoffline"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // yt-dlp/python embarque des binaires natifs par ABI.
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.common.core)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    implementation(project(":features:player:api"))
    implementation(project(":features:player:domain"))
    implementation(project(":features:player:data"))
    implementation(project(":features:player:ui"))

    implementation(project(":core:api"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    // yt-dlp : init de la lib au démarrage (MyApp)
    implementation(libs.youtubedl.android)
    implementation(libs.youtubedl.ffmpeg)
}