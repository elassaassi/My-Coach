# ADR-002 : Spring Modulith pour l'isolation des modules

## Statut
Accepté

## Contexte
L'application comprend 8 domaines métier distincts (user, matching, activity, messaging, rating, coaching, highlight, scouting). Sans frontière technique forte, les dépendances croisées prolifèrent silencieusement et rendent un éventuel découpage en microservices difficile.

## Décision
Utiliser **Spring Modulith 2.0.0** pour :
- Déclarer les modules comme packages racines (`org.elas.momentum.<module>`).
- Interdire les imports directs entre modules (vérifié par `ModuleStructureTest` au build).
- Exposer uniquement ce qui est nécessaire via des classes dans le package racine du module.

## Conséquences
**Positif**
- Les violations de frontières sont détectées à la compilation/test, pas en production.
- L'architecture reste monolithique déployable tout en étant « microservices-ready ».
- La documentation des dépendances inter-modules est générée automatiquement.

**Négatif**
- Les `@ApplicationModuleTest` nécessitent une configuration soigneuse des mocks inter-modules.
- Spring Modulith est encore récent : certaines fonctionnalités (Scenario tests) sont en évolution.