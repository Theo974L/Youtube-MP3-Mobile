import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../data/library_model.dart';
import '../data/models.dart';
import '../data/playlist_model.dart';
import 'game_play_screen.dart';

class GameHomeScreen extends StatelessWidget {
  const GameHomeScreen({super.key});

  Future<void> _start(BuildContext context, List<Track> pool) async {
    if (pool.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Aucun morceau jouable ici.')),
      );
      return;
    }
    Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => GamePlayScreen(pool: pool)),
    );
  }

  @override
  Widget build(BuildContext context) {
    final playlists = context.watch<PlaylistModel>().playlists;
    final library = context.watch<LibraryModel>().tracks;
    final scheme = Theme.of(context).colorScheme;

    return ListView(
      padding: const EdgeInsets.fromLTRB(20, 20, 20, 24),
      children: [
        Text('Blind Test 🎧',
            style: Theme.of(context)
                .textTheme
                .headlineMedium
                ?.copyWith(fontWeight: FontWeight.bold)),
        const SizedBox(height: 4),
        Text(
          'Devine 10 titres à partir d’extraits de plus en plus longs. '
          'Plus tu trouves vite et juste, plus tu marques !',
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: scheme.onSurfaceVariant,
              ),
        ),
        const SizedBox(height: 20),

        // Toute la bibliothèque
        _SourceTile(
          icon: Icons.library_music,
          title: 'Toute la bibliothèque',
          subtitle:
              '${library.length} ${library.length > 1 ? "morceaux" : "morceau"}',
          enabled: library.isNotEmpty,
          onTap: () => _start(context, library),
        ),
        const SizedBox(height: 18),

        Text('Depuis une playlist',
            style: Theme.of(context)
                .textTheme
                .titleMedium
                ?.copyWith(fontWeight: FontWeight.w600)),
        const SizedBox(height: 10),

        if (playlists.isEmpty)
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 8),
            child: Text('Aucune playlist pour l’instant.',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: scheme.onSurfaceVariant,
                    )),
          )
        else
          ...playlists.map((pl) => Padding(
                padding: const EdgeInsets.only(bottom: 10),
                child: _SourceTile(
                  icon: Icons.queue_music,
                  title: pl.name,
                  subtitle:
                      '${pl.trackCount} ${pl.trackCount > 1 ? "morceaux" : "morceau"}',
                  enabled: pl.trackCount > 0,
                  onTap: () async {
                    final tracks = await context
                        .read<PlaylistModel>()
                        .tracksOf(pl.id);
                    if (context.mounted) _start(context, tracks);
                  },
                ),
              )),
      ],
    );
  }
}

class _SourceTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final bool enabled;
  final VoidCallback onTap;

  const _SourceTile({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.enabled,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Opacity(
      opacity: enabled ? 1 : 0.5,
      child: Material(
        color: scheme.surfaceVariant,
        borderRadius: BorderRadius.circular(16),
        child: InkWell(
          borderRadius: BorderRadius.circular(16),
          onTap: enabled ? onTap : null,
          child: Padding(
            padding: const EdgeInsets.all(14),
            child: Row(
              children: [
                Container(
                  width: 48,
                  height: 48,
                  decoration: BoxDecoration(
                    color: scheme.primary.withOpacity(0.15),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(icon, color: scheme.primary),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(title,
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                          style: Theme.of(context)
                              .textTheme
                              .titleSmall
                              ?.copyWith(fontWeight: FontWeight.w600)),
                      Text(subtitle,
                          style: Theme.of(context).textTheme.bodySmall),
                    ],
                  ),
                ),
                const Icon(Icons.play_circle_fill),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
