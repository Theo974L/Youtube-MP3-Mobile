import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';
import 'package:provider/provider.dart';

import '../playback/player_service.dart';

class FullPlayerScreen extends StatefulWidget {
  const FullPlayerScreen({super.key});

  @override
  State<FullPlayerScreen> createState() => _FullPlayerScreenState();
}

class _FullPlayerScreenState extends State<FullPlayerScreen> {
  double? _dragMs;

  String _fmt(Duration d) {
    final m = d.inMinutes;
    final s = d.inSeconds % 60;
    return '$m:${s.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    final player = context.watch<PlayerService>();
    final track = player.current;
    final scheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.keyboard_arrow_down),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: const Text('Lecture en cours'),
        centerTitle: true,
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              const Spacer(),
              // Pochette
              AspectRatio(
                aspectRatio: 1,
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(24),
                  child: Container(
                    color: scheme.primary.withOpacity(0.15),
                    child: track?.thumbnailUrl != null
                        ? CachedNetworkImage(
                            imageUrl: track!.thumbnailUrl!,
                            fit: BoxFit.cover,
                            errorWidget: (_, __, ___) =>
                                const Icon(Icons.music_note, size: 96),
                          )
                        : const Icon(Icons.music_note, size: 96),
                  ),
                ),
              ),
              const SizedBox(height: 32),
              Text(
                track?.title ?? '—',
                textAlign: TextAlign.center,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: Theme.of(context)
                    .textTheme
                    .titleLarge
                    ?.copyWith(fontWeight: FontWeight.bold),
              ),
              if (track?.artist != null) ...[
                const SizedBox(height: 4),
                Text(
                  track!.artist!,
                  textAlign: TextAlign.center,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                        color: scheme.onSurfaceVariant,
                      ),
                ),
              ],
              const SizedBox(height: 24),
              _SeekBar(player: player, dragMs: _dragMs, fmt: _fmt, onDrag: (v) {
                setState(() => _dragMs = v);
              }, onDragEnd: (v) {
                player.seek(Duration(milliseconds: v.round()));
                setState(() => _dragMs = null);
              }),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  IconButton(
                    onPressed: player.toggleShuffle,
                    icon: Icon(Icons.shuffle,
                        color: player.shuffle
                            ? scheme.primary
                            : scheme.onSurfaceVariant),
                  ),
                  IconButton(
                    iconSize: 40,
                    onPressed: player.previous,
                    icon: const Icon(Icons.skip_previous),
                  ),
                  Material(
                    color: scheme.primary,
                    shape: const CircleBorder(),
                    child: IconButton(
                      iconSize: 40,
                      color: scheme.onPrimary,
                      onPressed: player.toggle,
                      icon: Icon(
                          player.isPlaying ? Icons.pause : Icons.play_arrow),
                    ),
                  ),
                  IconButton(
                    iconSize: 40,
                    onPressed: player.next,
                    icon: const Icon(Icons.skip_next),
                  ),
                  IconButton(
                    onPressed: player.cycleLoop,
                    icon: Icon(
                      player.loopMode == LoopMode.one
                          ? Icons.repeat_one
                          : Icons.repeat,
                      color: player.loopMode == LoopMode.off
                          ? scheme.onSurfaceVariant
                          : scheme.primary,
                    ),
                  ),
                ],
              ),
              const Spacer(),
            ],
          ),
        ),
      ),
    );
  }
}

class _SeekBar extends StatelessWidget {
  final PlayerService player;
  final double? dragMs;
  final String Function(Duration) fmt;
  final ValueChanged<double> onDrag;
  final ValueChanged<double> onDragEnd;

  const _SeekBar({
    required this.player,
    required this.dragMs,
    required this.fmt,
    required this.onDrag,
    required this.onDragEnd,
  });

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return StreamBuilder<Duration>(
      stream: player.positionStream,
      builder: (context, snapshot) {
        final duration = player.duration;
        final maxMs =
            duration.inMilliseconds > 0 ? duration.inMilliseconds.toDouble() : 1.0;
        final posMs = (snapshot.data ?? Duration.zero).inMilliseconds.toDouble();
        final value = (dragMs ?? posMs).clamp(0.0, maxMs).toDouble();
        return Column(
          children: [
            Slider(
              value: value,
              max: maxMs,
              onChanged: onDrag,
              onChangeEnd: onDragEnd,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(fmt(Duration(milliseconds: value.round())),
                    style: Theme.of(context).textTheme.labelMedium?.copyWith(
                        color: scheme.onSurfaceVariant)),
                Text(fmt(duration),
                    style: Theme.of(context).textTheme.labelMedium?.copyWith(
                        color: scheme.onSurfaceVariant)),
              ],
            ),
          ],
        );
      },
    );
  }
}
