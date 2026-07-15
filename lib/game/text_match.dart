import 'dart:math';

/// Nettoie un titre YouTube pour la comparaison :
/// minuscules, retire le contenu entre parenthèses/crochets, la ponctuation,
/// et les mots parasites (official, video, lyrics…).
String normalizeTitle(String input) {
  var t = input.toLowerCase();
  t = t.replaceAll(RegExp(r'[\(\[\{][^\)\]\}]*[\)\]\}]'), ' ');
  t = t.replaceAll(RegExp(r'[^a-z0-9à-ÿ\s]'), ' ');
  const noise = {
    'official', 'officiel', 'officielle', 'video', 'vidéo', 'audio', 'lyrics',
    'lyric', 'paroles', 'clip', 'hd', 'hq', '4k', 'mv', 'remastered',
    'remaster', 'ft', 'feat', 'featuring', 'music', 'musique', 'vevo', 'the',
    'le', 'la', 'les', 'prod',
  };
  final words = t
      .split(RegExp(r'\s+'))
      .where((w) => w.isNotEmpty && !noise.contains(w))
      .toList();
  return words.join(' ').trim();
}

int _levenshtein(String a, String b) {
  if (a == b) return 0;
  if (a.isEmpty) return b.length;
  if (b.isEmpty) return a.length;
  final prev = List<int>.generate(b.length + 1, (i) => i);
  final curr = List<int>.filled(b.length + 1, 0);
  for (var i = 0; i < a.length; i++) {
    curr[0] = i + 1;
    for (var j = 0; j < b.length; j++) {
      final cost = a[i] == b[j] ? 0 : 1;
      curr[j + 1] = [curr[j] + 1, prev[j + 1] + 1, prev[j] + cost].reduce(min);
    }
    for (var j = 0; j <= b.length; j++) {
      prev[j] = curr[j];
    }
  }
  return prev[b.length];
}

/// Similarité 0.0 → 1.0 entre la réponse du joueur et le vrai titre.
/// Combine ratio de Levenshtein, recouvrement de mots (Jaccard) et
/// "contenance" (la réponse est-elle incluse dans le titre) — on garde le max.
double titleSimilarity(String guess, String title) {
  final g = normalizeTitle(guess);
  final t = normalizeTitle(title);
  if (g.isEmpty || t.isEmpty) return 0;

  final dist = _levenshtein(g, t);
  final maxLen = max(g.length, t.length);
  final lev = maxLen == 0 ? 0.0 : 1 - dist / maxLen;

  final gs = g.split(' ').toSet();
  final ts = t.split(' ').toSet();
  final inter = gs.intersection(ts).length;
  final union = gs.union(ts).length;
  final jaccard = union == 0 ? 0.0 : inter / union;

  // La réponse est-elle largement incluse dans le titre ?
  final contain = gs.isEmpty ? 0.0 : inter / gs.length;
  // On pénalise un peu les réponses trop courtes (1 mot) pour éviter les matchs triviaux.
  final containScaled = gs.length >= 2 ? contain : contain * 0.7;

  return [lev, jaccard, containScaled].reduce(max);
}
