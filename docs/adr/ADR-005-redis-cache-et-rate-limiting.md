# ADR-005 : Redis pour les compteurs de likes et le rate limiting

## Statut
Accepté

## Contexte
Les compteurs de likes des highlights nécessitent des incréments atomiques à haute fréquence sans écrire en base à chaque requête. Le rate limiting par IP doit survivre à un redémarrage de l'application et ne pas consommer de mémoire JVM illimitée.

## Décision
**Likes** (`highlight` module)
- Compteurs stockés dans Redis : clé `highlight:likes:<id>`, opération `INCR`/`DECR`.
- Synchronisation périodique vers PostgreSQL via `@Scheduled` (toutes les 5 minutes).
- `RedisLikeCounterAdapter` annotée `@Profile("!test")` ; `NoOpHighlightLikeCounterAdapter` pour les tests.

**Rate limiting** (`RateLimitFilter`)
- **Bucket4j 8.10.1** : algorithme token bucket, 60 requêtes/minute par IP.
- Buckets stockés en mémoire JVM dans un **LRU borné** (`LinkedHashMap`, max 50 000 entrées) pour éviter l'épuisement mémoire.
- L'IP est extraite de `request.getRemoteAddr()` uniquement — `X-Forwarded-For` ignoré (non fiable sans reverse-proxy de confiance explicite).

## Conséquences
**Positif**
- Les incréments de likes sont non bloquants et n'impactent pas les requêtes métier.
- Le rate limiting protège contre les attaques par déni de service sans dépendance Redis synchrone.

**Négatif**
- Si Redis est indisponible, les compteurs de likes tombent en mode dégradé (NoOp).
- Le LRU en mémoire JVM signifie que les buckets ne survivent pas à un redémarrage (acceptable pour du rate limiting).
- En cluster multi-instances, le rate limiting par IP n'est pas partagé entre instances (à corriger via Bucket4j + Redis si nécessaire).