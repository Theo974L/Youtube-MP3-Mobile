package com.example.theolaforgeeval.player.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.theolaforgeeval.player.data.local.dao.PlaylistDao
import com.example.theolaforgeeval.player.data.local.dao.TrackDao
import com.example.theolaforgeeval.player.model.PlaylistEntity
import com.example.theolaforgeeval.player.model.PlaylistTrackCrossRef
import com.example.theolaforgeeval.player.model.TrackEntity

/**
 * Base Room dédiée à la bibliothèque YT.
 * v2 : ajout des playlists (migration non destructive -> les morceaux sont conservés).
 */
@Database(
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackCrossRef::class],
    version = 2,
    exportSchema = false,
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        const val NAME = "yt_offline.db"

        /** v1 -> v2 : crée les tables playlists sans toucher à `tracks`. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `playlists` " +
                        "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `playlist_track` " +
                        "(`playlistId` INTEGER NOT NULL, `trackId` INTEGER NOT NULL, " +
                        "`addedAt` INTEGER NOT NULL, PRIMARY KEY(`playlistId`, `trackId`), " +
                        "FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`trackId`) REFERENCES `tracks`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_playlist_track_trackId` " +
                        "ON `playlist_track` (`trackId`)",
                )
            }
        }
    }
}
