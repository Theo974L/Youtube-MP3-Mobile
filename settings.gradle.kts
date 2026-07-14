pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // NewPipeExtractor
    }
}


rootProject.name = "TheoLaforgeEval"

include(
    ":app",
    ":core:api", ":core:data", ":core:domain", "core:ui",":core:system",
    ":features:player:api", ":features:player:data", ":features:player:domain", ":features:player:ui"

)
 