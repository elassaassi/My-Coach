# ADR-006 : Virtual threads Java 25

## Statut
Accepté

## Contexte
Momentum est une application principalement I/O-bound (PostgreSQL, Redis, appels HTTP vers des services tiers potentiels). Le modèle thread-per-request classique avec des threads de plateforme limite le débit sous forte charge.

## Décision
Activer les virtual threads via `spring.threads.virtual.enabled=true` (Spring Boot 4.x).

Un bean `VirtualThreadTaskExecutor` est déclaré dans `VirtualThreadConfig` pour l'exécution asynchrone (`@EnableAsync`). Le nom du bean est `virtualThreadExecutor` (et non `asyncExecutor` pour éviter la confusion avec les pools classiques).

## Conséquences
**Positif**
- Les requêtes en attente I/O ne bloquent pas de thread de plateforme OS.
- Débit augmenté sans augmenter la taille du pool de threads.
- Compatible nativement avec Spring MVC et les filtres Servlet.

**Négatif**
- `synchronized` bloquant peut encore provoquer du « pinning » de carrier thread (à éviter dans les sections critiques).
- Certaines bibliothèques natives non compatibles peuvent présenter des comportements inattendus (à surveiller via JFR).
- Java 25 n'a pas de JRE Alpine stable : l'image Docker utilise `eclipse-temurin:24` en runtime.