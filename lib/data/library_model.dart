import 'dart:io';

import 'package:flutter/foundation.dart';

import 'database.dart';
import 'models.dart';
import 'youtube_service.dart';

/// État de la bibliothèque + logique de téléchargement (ChangeNotifier).
class LibraryModel extends ChangeNotifier {
  final AppDatabase _db;
  final YoutubeService _yt;

  LibraryModel(this._db, this._yt) {
    refresh();
  }

  List<Track> tracks = [];
  int totalBytes = 0;
  final Set<String> _downloading = {};
  String? message;

  bool isDownloading(String videoId) => _downloading.contains(videoId);

  Future<void> refresh() async {
    tracks = await _db.allTracks();
    totalBytes = await _db.totalBytes();
    notifyListeners();
  }

  Future<void> download(String videoId) async {
    if (_downloading.contains(videoId)) return;
    if (await _db.existsByYoutubeId(videoId)) {
      message = 'Déjà dans la bibliothèque';
      notifyListeners();
      return;
    }
    _downloading.add(videoId);
    notifyListeners();
    try {
      final track = await _yt.download(videoId);
      await _db.insertTrack(track);
      message = '« ${track.title} » ajouté';
      await refresh();
    } catch (_) {
      message = 'Échec du téléchargement';
    } finally {
      _downloading.remove(videoId);
      notifyListeners();
    }
  }

  Future<void> delete(Track t) async {
    try {
      final f = File(t.filePath);
      if (f.existsSync()) f.deleteSync();
    } catch (_) {}
    await _db.deleteTrack(t.id);
    await refresh();
  }

  void consumeMessage() {
    message = null;
  }
}
