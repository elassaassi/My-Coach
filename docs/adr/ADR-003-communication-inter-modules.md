# ADR-003 : Communication inter-modules via API publique

## Statut
Accepté

## Contexte
Les modules ont des besoins de lecture croisée : `activity` a besoin des profils utilisateurs, `scouting` a besoin des scores de `rating`, etc. Sans convention claire, ces accès se font directement sur les repositories JPA d'autres modules, cassant l'isolation.

## Décision
Deux mécanismes exclusifs pour communiquer entre modules :

**1. API publique synchrone** — classe dans le package racine du module fournisseur :
```
org.elas.momentum.user.UserModuleAPI       — lecture de profils
org.elas.momentum.user.AuthenticateUserUseCase
```
Le module consommateur injecte l'interface ; le module fournisseur en contrôle l'implémentation.

**2. Événements de domaine** — pour la communication asynchrone :
```
org.elas.momentum.rating.RatingSubmittedEvent
```
L'événement est dans le package racine du module émetteur. Les listeners sont dans les modules abonnés.

**Interdit** : import direct d'un repository, entity ou use case interne d'un autre module.

## Conséquences
**Positif**
- Les dépendances inter-modules sont explicites, versionnées et testables.
- Un module peut changer son implémentation interne sans impacter les consommateurs.
- Facilite la migration future vers des microservices (remplacer l'appel direct par un appel HTTP/Kafka).

**Négatif**
- Nécessite de maintenir les API publiques comme contrat stable.
- Les batch lookups (`findByIds`) doivent être ajoutés à l'API dès qu'un N+1 apparaît.