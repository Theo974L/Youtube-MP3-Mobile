# Feature `player` — Phase 2 (extraction + téléchargement)

Module ajouté **dans ton archi existante** : multi-module clean archi, **Koin**, package
`com.example.theolaforgeeval`, calqué sur `features/client/home`.

> ⚙️ **MAJ extraction — yt-dlp.** NewPipe + FFmpeg-kit ont été **remplacés** par
> **youtubedl-android** (`io.github.junkfood02.youtubedl-android`), qui embarque **yt-dlp** :
> il télécharge ET convertit en MP3 en une commande (`-x --audio-format mp3`), et **se met à
> jour tout seul** (`YoutubeDL.updateYoutubeDL` dans `MyApp`) — donc l'extraction survit aux
> changements de YouTube. Init de la lib au démarrage dans `MyApp` (thread en fond).
> `AudioExtractor` = `YtDlpExtractor` (métadonnées via `getInfo`) ; le `DownloadWorker` lance
> yt-dlp. Config requise : `android:extractNativeLibs="true"` + `abiFilters` (déjà en place).
> APK plus lourd (python + ffmpeg par ABI) — normal, c'est le prix de la fiabilité.
> Les sections « NewPipe / FFmpeg » plus bas sont **obsolètes**, gardées pour l'historique.

## Modules

```
features/player/
├── domain/   # abstractions pures (aucune dépendance Android/data)
│   ├── model/Track.kt              # TrackEntity (@Entity Room) + Track (modèle UI) + toDomain()
│   ├── model/ExtractedAudio.kt     # ExtractedAudio + ExtractionException
│   ├── extractor/AudioExtractor.kt # interface : extract(url) -> ExtractedAudio
│   ├── download/TrackDownloader.kt # interface + DownloadStatus (Idle/Running/Success/Failed)
│   └── repository/TrackRepository.kt
├── data/     # implémentations
│   ├── remote/NewPipeDownloader.kt     # Downloader NewPipe sur OkHttp
│   ├── remote/YouTubeExtractor.kt      # : AudioExtractor (NewPipeExtractor)
│   ├── local/dao/TrackDao.kt
│   ├── local/database/MusicDatabase.kt # Room dédiée (séparée de l'AppDatabase de home)
│   ├── repository/TrackRepositoryImpl.kt
│   ├── download/DownloadWorker.kt          # CoroutineWorker + KoinComponent (by inject)
│   └── download/WorkManagerTrackDownloader.kt # : TrackDownloader (mappe WorkInfo -> DownloadStatus)
├── ui/       # dépend UNIQUEMENT de domain
│   └── ui/screen/search/{SearchScreen,SearchViewModel,SearchUiState}.kt
└── api/      # module Koin d'assemblage
    └── api/PlayerModule.kt
```

## Flux

```
SearchScreen ─ koinViewModel ─▶ SearchViewModel
   ├─ preview()  ─▶ AudioExtractor.extract()      (aperçu titre/durée/miniature)
   └─ download() ─▶ TrackDownloader.enqueue()  ─▶ WorkManager ─▶ DownloadWorker
                        observeStatus(id) ◀── WorkInfo (Running/Success/Failed)
                                                       │
DownloadWorker (KoinComponent) : extract ▶ OkHttp download ▶ filesDir/music/<id>.<ext> ▶ Room
```

## Ce qui a été touché dans ton projet existant

- `settings.gradle.kts` : `include(":features:player:*")` + dépôt **JitPack** (NewPipe)
- `gradle/libs.versions.toml` : versions/libs `newpipe-extractor`, `okhttp`, `androidx-work-runtime-ktx` + bundle `player-download`
- `app/build.gradle.kts` : `implementation(project(":features:player:*"))`
- `MyApp.kt` : ajout de `PlayerModule` dans `startKoin { modules(...) }`
- `navhost/Screen.kt` + `appNavHost.kt` : route `Screen.Search` → `SearchScreen`
- `AndroidManifest.xml` : rien à faire, permission INTERNET déjà présente ✅

## Choix techniques (par rapport au README d'origine)

- **Koin au lieu de Hilt** : pour rester sur ta stack. `DownloadWorker` récupère ses deps via
  `KoinComponent` + `by inject()` → pas besoin de WorkerFactory custom.
- **OkHttp** ajouté (client bloquant requis par le `Downloader` de NewPipe ; Ktor est suspend/async
  et se marie mal avec l'API bloquante de NewPipe).
- **WorkManager 2.10.0** pour `getWorkInfoByIdFlow`.
- `collectAsState()` (comme tes autres écrans), pas `collectAsStateWithLifecycle`.

## Pour l'essayer

Navigue vers `Screen.Search.route` depuis ta bottom-bar / un bouton, colle un lien YouTube,
« Aperçu » puis « Télécharger ». Le fichier atterrit dans `filesDir/music/`, l'entrée en base Room.

## Transcodage MP3 (FFmpeg)

YouTube ne sert jamais de MP3 (flux = AAC `.m4a` ou Opus `.webm`). Le `DownloadWorker` :
1. télécharge le flux brut dans `cacheDir` (temporaire),
2. le transcode en `.mp3` via `AudioTranscoder` (impl. `FfmpegAudioTranscoder`, encodeur LAME `-q:a 2` ≈ 190 kbps, + métadonnées titre/artiste),
3. supprime le temporaire, enregistre le `.mp3` dans `filesDir/music/` + Room.

Dépendance : `com.moizhassan.ffmpeg:ffmpeg-kit-16kb` (fork **compatible 16 KB / API 35+**,
requis vu `targetSdk 36` — l'`ffmpeg-kit` original archivé en 2025 casserait sur Play).

> ⚠️ **Taille APK** : FFmpeg embarque des binaires natifs multi-ABI (~plusieurs dizaines de Mo).
> Pour une app perso c'est acceptable ; sinon activer les *ABI splits*.
>
> ⚠️ **Si le transcodage échoue** avec « Unknown encoder 'libmp3lame' », la variante de l'AAR ne
> contient pas LAME → il faut une variante `full` / `full-gpl` / `audio` (AAR dans `app/libs/`).
> Le code remonte proprement l'erreur « Transcodage MP3 échoué ».

## Thème clair / sombre

`core/ui` corrigé : le schéma clair utilisait par erreur les couleurs sombres, et Material You
(`dynamicColor`) écrasait ta palette. Désormais `TheoLaforgeEvalTheme` suit le mode système
avec **ta** palette (clair = fond quasi-blanc + texte sombre ; sombre = bleu nuit + texte clair),
`dynamicColor = false` par défaut.

## Pièges connus

- **NewPipeExtractor casse quand YouTube change** : si l'extraction lâche, bump `newPipeExtractor`
  dans le catalog (dernière version : github.com/TeamNewPipe/NewPipeExtractor).
- minSdk 26 → pas besoin de core-library desugaring.
- App perso, non publiée : cette lib viole les CGU YouTube (usage strictement privé).
