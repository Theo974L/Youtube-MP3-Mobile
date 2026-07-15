import 'package:flutter/material.dart';

/// Palette (reprise de l'app native : bleu accent, fond bleu nuit en sombre).
class AppTheme {
  static const _seed = Color(0xFF4C8DFF);

  static ThemeData light() => _base(Brightness.light);
  static ThemeData dark() => _base(Brightness.dark);

  static ThemeData _base(Brightness brightness) {
    final scheme = ColorScheme.fromSeed(
      seedColor: _seed,
      brightness: brightness,
    );
    return ThemeData(
      colorScheme: scheme,
      useMaterial3: true,
      scaffoldBackgroundColor: scheme.surface,
    );
  }
}
