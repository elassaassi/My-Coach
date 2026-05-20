# ADR-001 : Architecture hexagonale (Ports & Adapters)

## Statut
Accepté

## Contexte
Momentum est une super-app sportive dont le cœur métier (matching, rating, highlight, coaching, scouting) est susceptible d'évoluer rapidement. Les règles métier ne doivent pas dépendre du framework, de la base de données ni du protocole HTTP afin de pouvoir être testées de façon isolée et migrées vers d'autres infrastructures sans réécriture.

## Décision
Chaque module suit l'architecture hexagonale stricte :

```
domain/
  model/        — entités, value objects, règles métier pures (0 annotation Spring)
  port/in/      — use cases (interfaces)
  port/out/     — ports de sortie (interfaces)
application/
  usecase/      — implémentations des use cases, orchestration uniquement
infrastructure/
  persistence/  — adapters JPA / Redis
  web/          — adapters HTTP (controllers)
```

**Règle dure** : le package `domain/` ne contient aucune annotation Spring (`@Component`, `@Transactional`, etc.).

## Conséquences
**Positif**
- Les règles métier peuvent être testées sans démarrer Spring ni la base de données.
- L'infrastructure est interchangeable (H2 en tests, PostgreSQL en prod).
- L'ajout de nouveaux canaux (gRPC, event-driven) ne touche pas le domaine.

**Négatif**
- Verbosité : chaque fonctionnalité requiert une interface port/in, une implémentation use case et un adapter.
- Courbe d'apprentissage pour les nouveaux contributeurs non familiers avec l'hexagone.