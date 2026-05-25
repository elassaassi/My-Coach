# Momentum — Documentation vivante

> Générée à partir du code, des tests et des ADRs. Mise à jour à chaque `./mvnw verify`.

---

## Index

| Section | Contenu |
|---------|---------|
| [Architecture](#architecture-modulith) | Diagrammes C4/PlantUML Spring Modulith |
| [ADRs](adr/README.md) | 11 décisions architecturales documentées |
| [Contrats API](#contrats-api) | Endpoints par domaine métier |
| [Spécifications BDD](#spécifications-bdd) | Scénarios Cucumber exécutables |
| [Couverture de tests](#couverture-de-tests) | JaCoCo + Mutation testing (PIT) |
| [Règles de développement](../CLAUDE.md) | 34 règles ARCH/SEC/SOLID/CRAFT/PERF/TESTS |

---

## Architecture Modulith

8 modules Spring Modulith — architecture hexagonale stricte (0 annotation Spring dans `domain/`).

Diagrammes PlantUML générés automatiquement : [`architecture/`](architecture/)

### Dépendances inter-modules

```
user       ← matching, activity, config
rating     ← scouting (via RatingSubmittedEvent)
shared     ← tous les modules
```

Règle ARCH-04 : communication inter-modules exclusivement via l'API publique du module racine ou des événements de domaine.

| Module | Rôle | Dépend de |
|--------|------|-----------|
| `user` | Inscription, profil sportif, avatar | shared, config |
| `activity` | Création, join/leave, statuts | shared, user |
| `matching` | Algorithme de mise en relation (score 50+30+20) | shared, user |
| `messaging` | Conversations et messages | shared |
| `rating` | Notation post-match, proScore, leaderboard | shared |
| `coaching` | Coachs, offres, réservations entreprises | shared |
| `highlight` | Pics sportifs, like Redis, highlight du jour | shared |
| `scouting` | Talents, recruteurs, intérêts de scouting | shared, rating (event) |

→ Diagramme complet : [`architecture/components.puml`](architecture/components.puml)

### Regénérer les diagrammes

```bash
./mvnw test -Dtest=ModuleStructureTest
# Sortie : target/spring-modulith-docs/*.puml
# Copier : cp target/spring-modulith-docs/*.puml docs/architecture/
```

---

## Contrats API

Format de réponse standard : `ApiResponse<T>` — `{ success, data, error, code, message }`

### Authentification

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/auth/login` | Connexion → JWT (24h) |
| POST | `/api/v1/users/register` | Inscription |
| GET | `/api/v1/users/me` | Mon profil |
| PUT | `/api/v1/users/me` | Mise à jour profil |
| POST | `/api/v1/users/me/avatar` | Upload avatar |

### Activités

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/activities` | Créer une activité |
| POST | `/api/v1/activities/{id}/join` | Rejoindre |
| POST | `/api/v1/activities/{id}/leave` | Quitter |
| GET | `/api/v1/activities/search` | Rechercher (sport, lieu, statut) |

### Rating & Leaderboard

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/ratings` | Noter un joueur post-match |
| GET | `/api/v1/ratings/stats/{userId}` | Statistiques d'un joueur |
| GET | `/api/v1/ratings/leaderboard` | Classement mono-sport |
| GET | `/api/v1/ratings/leaderboard/multi` | Classement multi-sport |

### Coaching

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/coaches` | Créer profil coach |
| GET | `/api/v1/coaches/search` | Chercher des coachs |
| POST | `/api/v1/coaches/{id}/offers` | Créer une offre |
| POST | `/api/v1/coaches/bookings` | Réserver une session |

### Highlights

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/highlights/upload` | Upload fichier (magic bytes, ffmpeg fallback) |
| POST | `/api/v1/highlights` | Publier un highlight |
| POST | `/api/v1/highlights/{id}/like` | Liker / unliker |
| GET | `/api/v1/highlights/today` | Highlight du jour |
| GET | `/api/v1/highlights/feed` | Feed des highlights |

### Scouting

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/v1/scouting/talents` | Chercher des talents |
| GET | `/api/v1/scouting/talents/pro` | Top talents (proScore ≥ 80) |
| POST | `/api/v1/scouting/interests` | Exprimer un intérêt recruteur |
| POST | `/api/v1/scouting/recruiters` | Créer profil recruteur |

### Messaging

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/messages` | Envoyer un message |
| GET | `/api/v1/messages/conversations` | Mes conversations |
| GET | `/api/v1/messages/conversations/{id}` | Messages d'une conversation |

→ Documentation interactive : `http://localhost:8080/swagger-ui.html` (serveur démarré)

---

## Spécifications BDD

Scénarios Cucumber dans [`src/test/resources/features/`](../src/test/resources/features/).

### User Registration (`features/user/user_registration.feature`)

| Scénario | Statut |
|----------|--------|
| Inscription nominale → statut PENDING_VERIFICATION | ✅ |
| Activation du compte → statut ACTIVE | ✅ |
| Email invalide → InvalidEmailException | ✅ |

### Activity (`features/activity/create_activity.feature`)

| Scénario | Statut |
|----------|--------|
| Créer une activité et rejoindre → 1 participant, OPEN | ✅ |
| Activité FULL quand max atteint | ✅ |
| Annuler une activité → CANCELLED | ✅ |

### Rating (`features/rating/rate_player.feature`)

| Scénario | Statut |
|----------|--------|
| Noter un joueur après complétion → rating créé | ✅ |
| Calcul du proScore > 50 avec stats | ✅ |
| Score 0 → IllegalArgumentException | ✅ |
| Score > 5 → IllegalArgumentException | ✅ |

```bash
# Exécuter les scénarios BDD
./mvnw test -Dgroups=bdd
```

---

## Couverture de tests

### JaCoCo (couverture par instructions)

| Périmètre | Seuil | Rapport |
|-----------|-------|---------|
| Domaine (`domain/model`, `domain/port`) | ≥ 80% | `target/site/jacoco/index.html` |

```bash
./mvnw verify
# Rapport : target/site/jacoco/index.html
```

### PIT Mutation Testing

| Métrique | Seuil | Signification |
|----------|-------|---------------|
| Mutation score | ≥ 65% | Mutations tuées / total |
| Coverage | ≥ 68% | Code couvert par les tests |
| Test strength | ≈ 85% | Mutations tuées / couvertes uniquement |

Périmètre : `domain/model/**`, `application/usecase/**`, `shared.FileValidator`

```bash
./mvnw clean test -P mutation
# Rapport : target/pit-reports/index.html
```

### Suite de tests actuelle (~110 tests)

| Catégorie | Tests |
|-----------|-------|
| Domain unit (no Spring) | ~70 |
| Controller (MockMvc standalone) | ~15 |
| FileValidator | 26 |
| BDD Cucumber | 10 |
| Modulith structure | 2 |

---

## Décisions architecturales

11 ADRs dans [`adr/`](adr/README.md) — extraits notables :

| ADR | Décision clé |
|-----|-------------|
| [ADR-001](adr/ADR-001-architecture-hexagonale.md) | Hexagonale : `domain/` sans Spring |
| [ADR-007](adr/ADR-007-validation-fichiers-magic-bytes.md) | Magic bytes — jamais `getContentType()` |
| [ADR-009](adr/ADR-009-proscore-formula.md) | `base×0.3/0.5/0.2 + min(MOTM×5, 30)` |
| [ADR-011](adr/ADR-011-gestion-exceptions-io-upload.md) | IOException scoped (Broken pipe uniquement) |
| [ADR-004](adr/ADR-004-jwt-maison-sans-refresh.md) | JWT sans refresh — réévaluation avant prod |