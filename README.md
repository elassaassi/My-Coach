# Momentum — Sports Social Platform

Momentum est une super-app sportive conçue pour la communauté marocaine et sa diaspora.
Backend Java 25 / Spring Boot 4.0.3 / Spring Modulith 2.0.0.

## Architecture

```
Spring Modulith — Architecture Hexagonale (Ports & Adapters)
├── user        — Inscription, authentification, profil sportif
├── activity    — Création et gestion d'activités sportives
├── matching    — Algorithme de mise en relation par score
├── messaging   — Conversations et messagerie
├── rating      — Notation post-match, leaderboard, proScore
├── coaching    — Coachs, offres, réservations entreprises
├── highlight   — Pics sportifs, like Redis, highlight du jour
└── scouting    — Talents, recruteurs, intérêts de scouting
```

## Stack Technique

| Couche | Technologie |
|--------|-------------|
| Runtime | Java 25, Virtual Threads |
| Framework | Spring Boot 4.0.3, Spring Modulith 2.0.0 |
| Persistence | PostgreSQL 16, Flyway, Spring Data JPA |
| Cache | Redis 7 |
| Sécurité | Spring Security, JJWT 0.12.6, Keycloak 25 (profile) |
| Observabilité | Actuator, Micrometer, Prometheus, ELK Stack |
| Documentation | Springdoc OpenAPI 2.8.6 |
| Tests | JUnit 5, Cucumber BDD, RestAssured, Testcontainers |
| Coverage | JaCoCo (objectif 80%) |
| Build | Maven 3.9 |

## Démarrage Local

### Prérequis
- Docker Desktop
- Java 25+
- Maven 3.9+

### Lancer l'infrastructure
```bash
docker compose up -d
```

Services démarrés :
- PostgreSQL : `localhost:5433`
- Redis : `localhost:6379`
- Keycloak : `localhost:8180`
- Elasticsearch : `localhost:9200`
- Kibana : `localhost:5601`

### Lancer l'application
```bash
./mvnw spring-boot:run
```


### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Authentification & Utilisateurs
```
POST /api/v1/auth/login           — Connexion JWT
POST /api/v1/users/register       — Inscription
GET  /api/v1/users/me/profile     — Mon profil
PUT  /api/v1/users/me/profile     — Mise à jour profil
```

### Activités Sportives
```
POST /api/v1/activities            — Créer une activité
POST /api/v1/activities/{id}/join  — Rejoindre
POST /api/v1/activities/{id}/leave — Quitter
GET  /api/v1/activities/search    — Rechercher
```

### Matching
```
POST /api/v1/matches               — Demande de match
GET  /api/v1/matches/me            — Mes demandes
```

### Rating & Leaderboard
```
POST /api/v1/ratings               — Noter un joueur
GET  /api/v1/ratings/stats/{userId} — Statistiques joueur
GET  /api/v1/ratings/leaderboard?sport=football — Classement
GET  /api/v1/ratings/leaderboard/multi?sports=football,tennis — Multi-sport
```

### Coaching
```
POST /api/v1/coaches               — Créer profil coach
GET  /api/v1/coaches/search        — Chercher coachs
POST /api/v1/coaches/{id}/offers   — Créer offre
POST /api/v1/coaches/bookings      — Réserver
```

### Highlights
```
POST /api/v1/highlights            — Publier highlight
POST /api/v1/highlights/{id}/like  — Liker
GET  /api/v1/highlights/today      — Highlight du jour
```

### Scouting
```
GET  /api/v1/scouting/talents      — Chercher talents
GET  /api/v1/scouting/talents/pro  — Top talents pro
POST /api/v1/scouting/interests    — Exprimer intérêt
POST /api/v1/scouting/recruiters   — Profil recruteur
```

## Tests

### Tests unitaires (domaine pur)
```bash
./mvnw test
```

### Tests avec couverture JaCoCo
```bash
./mvnw verify
# Rapport : target/site/jacoco/index.html
```

### Tests BDD Cucumber
```bash
./mvnw test -Dgroups=bdd
```

## Variables d'Environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `JWT_SECRET` | Clé HMAC JWT (256+ bits) | `momentum-secret-...` |
| `SPRING_DATASOURCE_URL` | JDBC URL PostgreSQL | `jdbc:postgresql://localhost:5433/momentum` |
| `SPRING_DATASOURCE_PASSWORD` | Password DB | `momentum` |
| `REDIS_HOST` | Hôte Redis | `localhost` |
| `REDIS_PORT` | Port Redis | `6379` |
| `KEYCLOAK_ISSUER_URI` | Issuer Keycloak (profile keycloak) | `http://localhost:8180/realms/momentum` |

## Déploiement Kubernetes (Helm)

```bash
# Local (minikube/k3d)
helm install momentum ./helm -f helm/values.yaml

# Recette
helm install momentum ./helm -f helm/values-recette.yaml

# Production
helm install momentum ./helm -f helm/values-prod.yaml
```

## Sécurité

- **JWT** : HMAC-SHA256, expiration 24h (Phase 0)
- **Keycloak** : OAuth2/OIDC Resource Server (profile `keycloak`), Google/Facebook SSO
- **Rate Limiting** : Bucket4j sur `/api/v1/auth/**` et `/api/v1/users/register` (20 req/min)
- **Headers** : HSTS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy
- **OWASP** : Scan CVE au build (`mvn dependency-check:check`)

## Algorithme de Matching

Score = sport(50) + niveau(30) + distance(20)

Niveaux compatibles si écart <= 1 :
`BEGINNER <-> INTERMEDIATE <-> ADVANCED <-> ELITE`

## proScore (Potentiel Pro)

Score calculé sur les statistiques de rating :
```
base = (behavior×0.3 + technicality×0.5 + teamwork×0.2) / 5 × 70
bonus = min(manOfMatch × 5, 30)
proScore = min(100, round(base + bonus))
```

## Roadmap

- **Phase 1** : Spring Cloud, Keycloak OIDC complet, notifications push
- **Phase 2** : 8 microservices, Kafka, Spring AI matching
- **Phase 3** : Mobile (React Native), marketplace partenaires
