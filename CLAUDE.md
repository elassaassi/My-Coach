# Momentum — Règles de développement

> Seuls les domaines marqués **[ON]** dans le tableau de contrôle s'appliquent.
> Pour activer/désactiver : *"active le domaine SECURITE"* ou modifie directement ce fichier.
> Les règles individuelles `[OFF]` à l'intérieur d'un domaine `[ON]` sont ignorées.

---

## Tableau de contrôle

| Domaine       | Statut | Périmètre                                              |
|---------------|--------|--------------------------------------------------------|
| ARCHITECTURE  | [ON]   | Hexagonale, Modulith, ports/adapters, transactions     |
| SECURITE      | [ON]   | Fichiers, injection, IDOR, auth, CORS                  |
| SOLID         | [ON]   | SRP, OCP, DIP, ISP                                     |
| CRAFT         | [ON]   | Nommage, commentaires, early return, immutabilité      |
| PERFORMANCE   | [ON]   | N+1, pagination, requêtes bornées, concurrence         |
| TESTS         | [ON]   | Couverture, isolation, cas limites                     |

---

## ARCHITECTURE

| ID      | Statut | Règle |
|---------|--------|-------|
| ARCH-01 | [ON]   | Le package `domain/` ne contient aucune annotation Spring (`@Component`, `@Service`, `@Transactional`, etc.) |
| ARCH-02 | [ON]   | Les controllers n'injectent jamais un repository — uniquement des ports `in/` (use cases) |
| ARCH-03 | [ON]   | Les use cases n'injectent jamais un repository JPA — uniquement des ports `out/` |
| ARCH-04 | [ON]   | Communication inter-modules exclusivement via l'API publique du module (package racine) ou des événements de domaine — jamais d'import interne |
| ARCH-05 | [ON]   | Toute méthode de lecture dans un use case est annotée `@Transactional(readOnly = true)` |
| ARCH-06 | [ON]   | Un use case = une interface port/in = une responsabilité — pas de use case fourre-tout |

---

## SECURITE

| ID     | Statut | Règle |
|--------|--------|-------|
| SEC-01 | [ON]   | Le type d'un fichier uploadé est déterminé par magic bytes uniquement — jamais par `getContentType()` ni l'extension cliente |
| SEC-02 | [ON]   | Le nom du fichier de destination est toujours `UUID.randomUUID() + ext` — jamais dérivé du nom fourni par le client |
| SEC-03 | [ON]   | Tout chemin de fichier est normalisé (`toAbsolutePath().normalize()`) et vérifié qu'il reste sous le répertoire cible (protection path traversal) |
| SEC-04 | [ON]   | `X-Forwarded-For` n'est pas utilisé sans reverse-proxy de confiance explicitement configuré — utiliser `request.getRemoteAddr()` |
| SEC-05 | [ON]   | Les endpoints cross-utilisateur renvoient un DTO public (`PublicUserResult`) sans email ni coordonnées GPS précises (protection IDOR) |
| SEC-06 | [ON]   | Pas d'interpolation de chaîne dans les requêtes JPQL/SQL — uniquement des paramètres nommés (`@Param`) |
| SEC-07 | [ON]   | La validation des entrées se fait à la frontière du système (controller/DTO) — pas dans le domaine |
| SEC-08 | [ON]   | Chaque endpoint modifiant une ressource vérifie que l'utilisateur connecté en est le propriétaire avant d'agir |

---

## SOLID

| ID       | Statut | Règle |
|----------|--------|-------|
| SOLID-01 | [ON]   | SRP — une classe = une raison de changer ; un use case = une opération métier |
| SOLID-02 | [ON]   | OCP — ajouter une fonctionnalité = créer un nouveau use case, pas modifier un existant |
| SOLID-03 | [ON]   | DIP — controllers et use cases dépendent d'interfaces, jamais de classes concrètes d'infrastructure |
| SOLID-04 | [ON]   | ISP — une interface port/in ne déclare que les méthodes nécessaires à son consommateur direct |

---

## CRAFT

| ID       | Statut | Règle |
|----------|--------|-------|
| CRAFT-01 | [ON]   | Pas de commentaire qui explique QUOI — uniquement POURQUOI (contrainte cachée, contournement de bug non évident) |
| CRAFT-02 | [ON]   | Early return systématique — pas de `else` après un `return`/`throw` |
| CRAFT-03 | [ON]   | Pas de magic number ni magic string — constantes nommées ou enums |
| CRAFT-04 | [ON]   | Les value objects et records du domaine sont immutables — pas de setter |
| CRAFT-05 | [ON]   | Pas de `catch (Exception e) {}` silencieux — soit rethrow, soit log structuré + rethrow |
| CRAFT-06 | [ON]   | Pas d'abstraction créée pour moins de 3 usages concrets (éviter la sur-ingénierie) |

---

## PERFORMANCE

| ID      | Statut | Règle |
|---------|--------|-------|
| PERF-01 | [ON]   | Pas de `findAll()` sans `Pageable` sur des tables pouvant dépasser 1 000 lignes |
| PERF-02 | [ON]   | Les batch lookups inter-modules utilisent `findByIds(Set<String>)` — jamais de `findById` en boucle (N+1) |
| PERF-03 | [ON]   | Les requêtes de feed/leaderboard sont bornées à 100 entrées maximum au niveau de la requête SQL |
| PERF-04 | [ON]   | Pas de `synchronized` dans du code exécuté sur virtual thread — utiliser des structures concurrentes (`ConcurrentHashMap`, `Semaphore`) |
| PERF-05 | [ON]   | Tout endpoint de liste expose une pagination (paramètres `page` + `size`) |

---

## TESTS

| ID      | Statut | Règle |
|---------|--------|-------|
| TEST-01 | [ON]   | Les tests unitaires du domaine ne démarrent pas Spring — pas de `@SpringBootTest` dans `domain/` |
| TEST-02 | [ON]   | Les mocks ne remplacent pas la base de données dans les tests d'intégration — H2 en mémoire ou Testcontainers |
| TEST-03 | [ON]   | Chaque use case public a au moins un test couvrant le chemin nominal et un chemin d'erreur |
| TEST-04 | [ON]   | La couverture du domaine (JaCoCo) ne descend pas sous 80 % |
| TEST-05 | [ON]   | Les cas limites (null, liste vide, ID inexistant) sont couverts par des tests dédiés |
| TEST-06 | [ON]   | Le mutation score du domaine (PIT) ne descend pas sous le seuil courant — `./mvnw clean test -P mutation` (rapport : `target/pit-reports/index.html`) |

---

## Commandes rapides

```
active SECURITE          → domaine SECURITE = [ON]
désactive TESTS          → domaine TESTS = [OFF]
active SEC-08            → règle individuelle SEC-08 = [ON]
désactive CRAFT-01       → règle CRAFT-01 = [OFF]
active tout              → tous les domaines = [ON]
désactive tout sauf SECURITE
statut des règles        → je liste les domaines et leur état actuel
```