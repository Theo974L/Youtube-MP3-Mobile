import 'package:flutter/foundation.dart';
import 'package:just_audio/just_audio.dart';

import '../data/models.dart';

/// Lecture audio via just_audio (Android + iOS). ChangeNotifier pour l'UI.
/// (La lecture en fond + notification sera ajoutée en partie 2 via audio_service.)
class PlayerService extends ChangeNotifier {
  final AudioPlayer _player = AudioPlayer();
  List<Track> _queue = [];

  PlayerService() {
    _player.playerStateStream.listen((_) => notifyListeners());
    _player.currentIndexStream.listen((_) => notifyListeners());
    _player.sequenceStateStream.listen((_) => notifyListeners());
  }

  Stream<Duration> get positionStream => _player.positionStream;

  bool get hasMedia => _queue.isNotEmpty && _player.currentIndex != null;

  Track? get current {
    final i = _player.currentIndex;
    if (i == null || i < 0 || i >= _queue.length) return null;
    return _queue[i];
  }

  bool get isPlaying => _player.playing;
  Duration get duration => _player.duration ?? Duration.zero;
  bool get shuffle => _player.shuffleModeEnabled;
  LoopMode get loopMode => _player.loopMode;

  Future<void> playQueue(List<Track> tracks, int index) async {
    _queue = List.of(tracks);
    final source = ConcatenatingAudioSource(
      children: tracks.map((t) => AudioSource.uri(Uri.file(t.filePath))).toList(),
    );
    await _player.setAudioSource(source, initialIndex: index);
    await _player.play();
    notifyListeners();
  }

  void toggle() => _player.playing ? _player.pause() : _player.play();
  void next() => _player.seekToNext();
  void previous() => _player.seekToPrevious();
  void seek(Duration position) => _player.seek(position);
  void toggleShuffle() =>
      _player.setShuffleModeEnabled(!_player.shuffleModeEnabled);

  void cycleLoop() {
    const modes = [LoopMode.off, LoopMode.all, LoopMode.one];
    final next = modes[(modes.indexOf(_player.loopMode) + 1) % modes.length];
    _player.setLoopMode(next);
  }

  @override
  void dispose() {
    _player.dispose();
    super.dispose();
  }
}
