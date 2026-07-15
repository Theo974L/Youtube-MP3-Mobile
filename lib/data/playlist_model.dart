import 'package:flutter/foundation.dart';

import 'database.dart';
import 'models.dart';

/// État des playlists (ChangeNotifier). sqflite n'est pas réactif, donc on
/// recharge après chaque modification.
class PlaylistModel extends ChangeNotifier {
  final AppDatabase _db;

  PlaylistModel(this._db) {
    refresh();
  }

  List<Playlist> playlists = [];

  Future<void> refresh() async {
    playlists = await _db.playlists();
    notifyListeners();
  }

  Future<void> create(String name) async {
    if (name.trim().isEmpty) return;
    await _db.createPlaylist(name.trim());
    await refresh();
  }

  Future<void> delete(int id) async {
    await _db.deletePlaylist(id);
    await refresh();
  }

  Future<void> addTrack(int playlistId, int trackId) async {
    await _db.addTrackToPlaylist(playlistId, trackId);
    await refresh();
  }

  Future<void> createAndAddTrack(String name, int trackId) async {
    final trimmed = name.trim();
    if (trimmed.isEmpty) return;
    final id = await _db.createPlaylist(trimmed);
    await _db.addTrackToPlaylist(id, trackId);
    await refresh();
  }

  Future<void> removeTrack(int playlistId, int trackId) async {
    await _db.removeTrackFromPlaylist(playlistId, trackId);
    await refresh();
  }

  Future<List<Track>> tracksOf(int playlistId) => _db.tracksOfPlaylist(playlistId);
}
