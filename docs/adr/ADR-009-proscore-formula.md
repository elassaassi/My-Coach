# ADR-009 : Formule de calcul du proScore

## Statut
Accepté

## Contexte
Le module `rating` doit produire un score numérique représentant le niveau sportif d'un joueur à partir d'évaluations pair-à-pair. Le score doit être interprétable (0–100), pondérer les dimensions clés du jeu et récompenser les performances exceptionnelles sans les laisser dominer le score technique.

## Décision
```
base  = (comportement×0,3 + technicité×0,5 + travail_équipe×0,2) / 5 × 70
bonus = min(votes_manOfMatch × 5, 30)
proScore = min(100, round(base + bonus))
```

- **Technicité** (50%) : dimension la plus discriminante pour le scouting.
- **Comportement** (30%) : fair-play, respect des règles.
- **Travail d'équipe** (20%) : collectif.
- **Bonus Man of the Match** : plafonné à 30 points pour éviter qu'un joueur populaire sans niveau technique atteigne 100.
- Le score est calculé par `PlayerRating.computeProScore()` dans le domaine — aucune annotation Spring.

Le `proScore` dérive le `PlayerLevel` selon les seuils :
- `< 30` → BEGINNER
- `30–54` → INTERMEDIATE
- `55–79` → ADVANCED
- `≥ 80` → ELITE

## Conséquences
**Positif**
- Formule transparente, explicable aux utilisateurs.
- Calcul pur dans le domaine, testable sans infrastructure.

**Négatif**
- Les poids (0.3/0.5/0.2) et seuils de niveau sont hardcodés ; un ajustement nécessite un déploiement.
- La plafonné du bonus à 30 est arbitraire et peut être contestée selon le retour utilisateur.