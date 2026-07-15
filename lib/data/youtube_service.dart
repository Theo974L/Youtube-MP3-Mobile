import 'dart:io';

import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
// On masque SearchResult/Playlist de la lib pour éviter la collision avec nos modèles.
import 'package:youtube_explode_dart/youtube_explode_dart.dart'
    hide SearchResult, Playlist;

import 'models.dart';

/// Extraction + recherche + téléchargement via youtube_explode_dart (Android + iOS).
class YoutubeService {
  final YoutubeExplode _yt = YoutubeExplode();

  void dispose() => _yt.close();

  /// Résout un lien (ou id) en un seul résultat.
  Future<SearchResult> resolve(String urlOrId) async {
    final v = await _yt.videos.get(urlOrId);
    return SearchResult(
      videoId: v.id.value,
      title: v.title,
      author: v.author,
      durationSec: v.duration?.inSeconds ?? 0,
      thumbnailUrl: v.thumbnails.highResUrl,
    );
  }

  Future<List<SearchResult>> search(String query) async {
    final results = await _yt.search.search(query);
    return results.map((v) {
      return SearchResult(
        videoId: v.id.value,
        title: v.title,
        author: v.author,
        durationSec: v.duration?.inSeconds ?? 0,
        thumbnailUrl: v.thumbnails.highResUrl,
      );
    }).toList();
  }

  /// Télécharge la piste audio (m4a/webm) dans le stockage privé de l'app.
  /// [onProgress] : 0.0 -> 1.0 (null si taille inconnue).
  Future<Track> download(
    String videoIdOrUrl, {
    void Function(double? progress)? onProgress,
  }) async {
    final video = await _yt.videos.get(videoIdOrUrl);
    // Client "androidVr" : ses URLs de flux ne sont PAS bridées par YouTube,
    // donc le téléchargement se fait à pleine vitesse (le client web est throttlé).
    final manifest = await _yt.videos.streamsClient.getManifest(
      video.id,
      ytClients: [YoutubeApiClient.androidVr],
    );
    final audio = manifest.audioOnly.withHighestBitrate();

    final dir = await getApplicationDocumentsDirectory();
    final musicDir = Directory(p.join(dir.path, 'music'))
      ..createSync(recursive: true);
    final ext = audio.container.name == 'mp4' ? 'm4a' : audio.container.name;
    final file = File(p.join(musicDir.path, '${video.id.value}.$ext'));

    final total = audio.size.totalBytes;
    var received = 0;
    final sink = file.openWrite();
    await for (final chunk in _yt.videos.streamsClient.get(audio)) {
      received += chunk.length;
      sink.add(chunk);
      if (onProgress != null) {
        onProgress(total > 0 ? received / total : null);
      }
    }
    await sink.flush();
    await sink.close();

    return Track(
      youtubeId: video.id.value,
      title: video.title,
      artist: video.author,
      durationSec: video.duration?.inSeconds ?? 0,
      filePath: file.path,
      fileSizeBytes: file.lengthSync(),
      thumbnailUrl: video.thumbnails.highResUrl,
    );
  }
}
