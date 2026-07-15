import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'core/theme.dart';
import 'data/database.dart';
import 'data/library_model.dart';
import 'data/playlist_model.dart';
import 'data/youtube_service.dart';
import 'playback/player_service.dart';
import 'ui/home_shell.dart';

void main() {
  runApp(const YtOfflineApp());
}

class YtOfflineApp extends StatelessWidget {
  const YtOfflineApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider<AppDatabase>(create: (_) => AppDatabase()),
        Provider<YoutubeService>(
          create: (_) => YoutubeService(),
          dispose: (_, s) => s.dispose(),
        ),
        ChangeNotifierProvider<LibraryModel>(
          create: (ctx) => LibraryModel(
            ctx.read<AppDatabase>(),
            ctx.read<YoutubeService>(),
          ),
        ),
        ChangeNotifierProvider<PlayerService>(create: (_) => PlayerService()),
        ChangeNotifierProvider<PlaylistModel>(
          create: (ctx) => PlaylistModel(ctx.read<AppDatabase>()),
        ),
      ],
      child: MaterialApp(
        title: 'YT Offline',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.light(),
        darkTheme: AppTheme.dark(),
        themeMode: ThemeMode.system,
        home: const HomeShell(),
      ),
    );
  }
}
