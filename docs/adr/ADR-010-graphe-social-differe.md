# ADR-010 : Base de données graphe pour le social graph — décision différée

## Statut
Différé

## Contexte
Plusieurs fonctionnalités futures (matching avancé par affinités communes, scouting via connexions indirectes, recommandations collaboratives « amis de mes amis ») bénéficieraient d'une traversée de graphe efficace que PostgreSQL gère mal au-delà de quelques niveaux de profondeur.

## Décision
**Ne pas implémenter** de base de données graphe (Neo4j ou équivalent) en Phase 0/1.

Raisons :
1. Le volume d'utilisateurs actuel ne justifie pas la complexité opérationnelle supplémentaire.
2. PostgreSQL avec des requêtes récursives (`WITH RECURSIVE`) suffit jusqu'à plusieurs dizaines de milliers de nœuds.
3. L'architecture hexagonale permet d'ajouter un microservice graphe alimenté par des événements sans modifier le domaine existant.

**Point de réévaluation** : pertinent à partir de ~100 000 utilisateurs actifs ou dès que les requêtes de matching multi-niveaux deviennent le goulot d'étranglement mesuré.

## Conséquences
**Positif**
- Simplicité opérationnelle : une seule base de données à gérer.
- Pas de synchronisation à maintenir entre PostgreSQL et un graphe.

**Négatif**
- Les requêtes de matching avancé resteront limitées en profondeur de traversée.
- Une migration future vers un graphe nécessitera un backfill des données historiques.