@file:Suppress("DEPRECATION")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.theolaforgeeval.core.ui"
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
    buildFeatures {
        compose = true
    }
    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(project.dependencies.platform(libs.koin.bom))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.ui.extras)
    implementation(libs.bundles.common.core)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.google)
    implementation(libs.bundles.system.controller)

    implementation(project(":core:domain"))
}