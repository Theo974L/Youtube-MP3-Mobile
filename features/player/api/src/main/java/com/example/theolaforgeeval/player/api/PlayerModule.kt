package com.example.theolaforgeeval.player.api

import androidx.room.Room
import com.example.theolaforgeeval.player.data.download.WorkManagerTrackDownloader
import com.example.theolaforgeeval.player.data.playback.Media3PlaybackController
import com.example.theolaforgeeval.player.data.local.dao.PlaylistDao
import com.example.theolaforgeeval.player.data.local.dao.TrackDao
import com.example.theolaforgeeval.player.data.local.database.MusicDatabase
import com.example.theolaforgeeval.player.data.remote.YtDlpExtractor
import com.example.theolaforgeeval.player.data.repository.PlaylistRepositoryImpl
import com.example.theolaforgeeval.player.data.repository.TrackRepositoryImpl
import com.example.theolaforgeeval.player.download.TrackDownloader
import com.example.theolaforgeeval.player.extractor.AudioExtractor
import com.example.theolaforgeeval.player.playback.PlaybackController
import com.example.theolaforgeeval.player.repository.PlaylistRepository
import com.example.theolaforgeeval.player.repository.TrackRepository
import com.example.theolaforgeeval.player.ui.screen.library.LibraryViewModel
import com.example.theolaforgeeval.player.ui.screen.playlist.PlaylistDetailViewModel
import com.example.theolaforgeeval.player.ui.screen.playlist.PlaylistsViewModel
import com.example.theolaforgeeval.player.ui.screen.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Module Koin de la feature "player".
 * À enregistrer dans MyApp.startKoin { modules(..., PlayerModule) }.
 */
val PlayerModule = module {

    // --- Extraction (yt-dlp) : métadonnées pour l'aperçu ---
    single<AudioExtractor> { YtDlpExtractor() }

    // --- Base Room (dédiée YT) ---
    single {
        Room.databaseBuilder(
            androidContext(),
            MusicDatabase::class.java,
            MusicDatabase.NAME
        )
            .addMigrations(MusicDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }
    single<TrackDao> { get<MusicDatabase>().trackDao() }
    single<PlaylistDao> { get<MusicDatabase>().playlistDao() }

    // --- Repositories ---
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get()) }

    // --- Téléchargement + conversion MP3 (WorkManager + yt-dlp) ---
    single<TrackDownloader> { WorkManagerTrackDownloader(androidContext()) }

    // --- Lecture audio (Media3) ---
    single<PlaybackController> { Media3PlaybackController(androidContext()) }

    // --- ViewModels ---
    viewModel { SearchViewModel(get(), get()) }
    viewModel { LibraryViewModel(get(), get(), get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { (playlistId: Int) -> PlaylistDetailViewModel(playlistId, get(), get()) }
}
