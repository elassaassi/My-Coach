# Diagrammes d'architecture — Spring Modulith

Générés automatiquement par `ModuleStructureTest#modulith_generateDocumentation()`.

## Fichiers

| Fichier | Contenu |
|---------|---------|
| [`components.puml`](components.puml) | Vue globale — tous les modules et leurs dépendances |
| [`module-user.puml`](module-user.puml) | Module user isolé |
| [`module-activity.puml`](module-activity.puml) | Module activity isolé |
| [`module-matching.puml`](module-matching.puml) | Module matching isolé |
| [`module-messaging.puml`](module-messaging.puml) | Module messaging isolé |
| [`module-rating.puml`](module-rating.puml) | Module rating isolé |
| [`module-coaching.puml`](module-coaching.puml) | Module coaching isolé |
| [`module-highlight.puml`](module-highlight.puml) | Module highlight isolé |
| [`module-scouting.puml`](module-scouting.puml) | Module scouting (écoute `RatingSubmittedEvent`) |
| [`module-shared.puml`](module-shared.puml) | Module shared |
| [`module-config.puml`](module-config.puml) | Module config |

## Rendu

Utiliser [PlantUML](https://plantuml.com/) ou le plugin IntelliJ PlantUML Integration.

```bash
# Rendu CLI (nécessite plantuml.jar)
java -jar plantuml.jar docs/architecture/components.puml
```

## Regénérer

```bash
./mvnw test -Dtest=ModuleStructureTest
cp target/spring-modulith-docs/*.puml docs/architecture/
```

## Dépendances actuelles

```
scouting  --[listens to]--> rating      (RatingSubmittedEvent — domaine event)
matching  --[uses]---------> user
activity  --[uses]---------> user
config    --[uses]---------> user
user      --[depends on]---> shared + config
*         --[depends on]---> shared     (tous les modules)
```

Violations détectées par `modulith_structureIsValid()` lors de `./mvnw test`.