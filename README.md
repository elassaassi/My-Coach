# Momentum — Backend API

> Super-app sportive pour la communauté marocaine et la diaspora.
> Backend **Java 25 / Spring Boot 4.0.3 / Spring Modulith 2.0.0** — Architecture hexagonale.

🔗 Frontend : [My-Coach-Frontend](https://github.com/elassaassi/My-Coach-Frontend)

---

## Stack technique

| Couche | Technologie |
|--------|-------------|
| Runtime | Java 25 · Virtual Threads |
| Framework | Spring Boot 4.0.3 · Spring Modulith 2.0.0 |
| Persistence | PostgreSQL 16 · Flyway · Spring Data JPA |
| Cache | Redis 7 |
| Sécurité | Spring Security 6 · JJWT 0.12.6 · OAuth2 (Google/Facebook) · Keycloak 25 |
| Rate Limiting | Bucket4j + Redis |
| Observabilité | Actuator · Micrometer · Prometheus · ELK Stack |
| Documentation | Springdoc OpenAPI 2.8.6 |
| Tests | JUnit 5 · Cucumber BDD · RestAssured · Testcontainers |
| Coverage | JaCoCo ≥ 80% |
| Build | Maven 3.9 |
| Infra | Docker Compose · Helm · Kubernetes |

---

## Architecture

```
Spring Modulith — Architecture Hexagonale (Ports & Adapters)

org.elas.momentum/
├── user/         — Inscription, authentification, profil sportif
├── activity/     — Création et gestion d'activités sportives
├── matching/     — Algorithme de mise en relation par score
├── messaging/    — Conversations et messagerie
├── rating/       — Notation post-match, leaderboard, proScore
├── coaching/     — Coachs, offres, réservations entreprises
├── highlight/    — Pics sportifs, like Redis, highlight du jour
├── scouting/     — Talents, recruteurs, intérêts de scouting
├── shared/       — GeoUtils, DomainEvent, exceptions, ApiResponse
└── config/       — Security, JWT, OAuth2, RateLimit, OpenAPI
```

Chaque module suit le pattern :
```
module/
├── domain/
│   ├── model/        — Agrégats purs (0 annotation Spring)
│   ├── port/in/      — Use cases (interfaces)
│   ├── port/out/     — Repositories (interfaces)
│   └── event/        — Domain events
├── application/
│   ├── usecase/      — Implémentations des use cases
│   └── dto/          — Commands et Results
└── infrastructure/
    ├── persistence/  — Entités JPA, mappers, adapters
    └── web/          — Controllers REST
```

---

## Démarrage local

### Prérequis



- Docker Desktop
- Java 25+ (`sdk install java 25-open`)
- Maven 3.9+

### 1. Lancer l'infrastructure

```bash
docker compose up -d
```

| Service | Port |
|---------|------|
| PostgreSQL | `5433` |
| Redis | `6379` |
| Keycloak | `8180` |
| Elasticsearch | `9200` |
| Kibana | `5601` |
| Logstash | `5044` |

### 2. Lancer l'application

```bash
./mvnw spring-boot:run
```

Application disponible sur `http://localhost:8080`

### 3. Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

### Authentification

```
POST /api/v1/auth/login                  — Connexion → JWT
POST /api/v1/users/register              — Inscription
GET  /api/v1/users/me                    — Mon profil
PUT  /api/v1/users/me                    — Mise à jour profil
```

### Social Login (OAuth2)

```
GET  /oauth2/authorization/google        — Redirect Google
GET  /oauth2/authorization/facebook      — Redirect Facebook
GET  /login/oauth2/code/{provider}       — Callback OAuth2 (auto)
```

### Activités sportives

```
POST /api/v1/activities                  — Créer une activité
POST /api/v1/activities/{id}/join        — Rejoindre
POST /api/v1/activities/{id}/leave       — Quitter
GET  /api/v1/activities/search           — Rechercher (sport, lieu, statut)
```

### Matching

```
POST /api/v1/matches                     — Demande de match
GET  /api/v1/matches/me                  — Mes demandes
```

### Rating & Leaderboard

```
POST /api/v1/ratings                     — Noter un joueur post-match
GET  /api/v1/ratings/stats/{userId}      — Statistiques d'un joueur
GET  /api/v1/ratings/leaderboard         — Classement (?sport=football)
GET  /api/v1/ratings/leaderboard/multi   — Multi-sport (?sports=football,tennis)
```

### Coaching

```
POST /api/v1/coaches                     — Créer profil coach
GET  /api/v1/coaches/search              — Chercher des coachs
POST /api/v1/coaches/{id}/offers         — Créer une offre
POST /api/v1/coaches/bookings            — Réserver une session
```

### Highlights

```
POST /api/v1/highlights                  — Publier un highlight
POST /api/v1/highlights/{id}/like        — Liker / unliker
GET  /api/v1/highlights/today            — Highlight du jour
GET  /api/v1/highlights/feed             — Feed des highlights
```

### Scouting

```
GET  /api/v1/scouting/talents            — Chercher des talents
GET  /api/v1/scouting/talents/pro        — Top talents (proScore ≥ 80)
POST /api/v1/scouting/interests          — Exprimer un intérêt recruteur
POST /api/v1/scouting/recruiters         — Créer profil recruteur
```

### Messaging

```
POST /api/v1/messages                    — Envoyer un message
GET  /api/v1/messages/conversations      — Mes conversations
GET  /api/v1/messages/conversations/{id} — Messages d'une conversation
```

---

## Tests

```bash
# Tests unitaires (domaine pur)
./mvnw test

# Tests + couverture JaCoCo (rapport : target/site/jacoco/index.html)
./mvnw verify

# Tests BDD Cucumber uniquement
./mvnw test -Dgroups=bdd
```

Couverture actuelle : **≥ 80%** (instructions, exclusions : infra, DTOs, config)

---

## Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `JWT_SECRET` | Clé HMAC JWT (256+ bits) | `momentum-secret-key-...` |
| `GOOGLE_CLIENT_ID` | Client ID Google OAuth2 | *(vide — social login désactivé)* |
| `GOOGLE_CLIENT_SECRET` | Secret Google OAuth2 | *(vide)* |
| `FACEBOOK_APP_ID` | App ID Facebook OAuth2 | *(vide)* |
| `FACEBOOK_APP_SECRET` | Secret Facebook OAuth2 | *(vide)* |
| `FRONTEND_URL` | URL de redirection OAuth2 | `http://localhost:4200` |
| `REDIS_HOST` | Hôte Redis | `localhost` |
| `REDIS_PORT` | Port Redis | `6379` |

Pour activer le social login :
```bash
GOOGLE_CLIENT_ID=xxx GOOGLE_CLIENT_SECRET=xxx ./mvnw spring-boot:run
```

---

## Sécurité

| Mesure | Détail |
|--------|--------|
| **JWT** | HMAC-SHA384, expiration 24h |
| **OAuth2** | Google, Facebook — `OAuth2ClientConfig` conditionnel (app démarre sans credentials) |
| **Keycloak** | Profile `keycloak` — Resource Server OIDC (`application-keycloak.yml`) |
| **Rate Limiting** | Bucket4j : 20 req/min sur `/api/v1/auth/**` et `/api/v1/users/register` |
| **Headers** | HSTS, X-Frame-Options: DENY, X-Content-Type-Options, Referrer-Policy |
| **OWASP CVE** | `mvn dependency-check:check` — rapport dans `target/dependency-check-report.html` |

---

## Algorithme de matching

```
Score = sport(50pts) + niveau(30pts) + distance(20pts)

Niveaux compatibles (écart ≤ 1) :
BEGINNER ↔ INTERMEDIATE ↔ ADVANCED ↔ ELITE
```

## proScore (potentiel pro)

```
base  = (behavior×0.3 + technicality×0.5 + teamwork×0.2) / 5 × 70
bonus = min(manOfMatch × 5, 30)
proScore = min(100, round(base + bonus))
```

---

## Déploiement Kubernetes (Helm)

```bash
# Local (minikube / k3d)
helm install momentum ./helm -f helm/values.yaml

# Recette
helm install momentum ./helm -f helm/values-recette.yaml

# Production
helm install momentum ./helm -f helm/values-prod.yaml
```

Manifests K8s directs disponibles dans `k8s/` (namespace, app, postgres, redis, ELK, monitoring).

---

## Roadmap

| Phase | Contenu |
|-------|---------|
| **Phase 0** ✅ | 8 modules Modulith, JWT, OAuth2, JaCoCo, ELK, Helm |
| **Phase 1** | Spring Cloud Gateway, Keycloak OIDC complet, notifications push |
| **Phase 2** | Migration 8 microservices, Kafka events, Spring AI matching |
| **Phase 3** | Mobile natif (Capacitor), marketplace partenaires, analytics |
