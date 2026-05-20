# Architecture Decision Records — Momentum

| N°  | Titre | Statut |
|-----|-------|--------|
| [ADR-001](ADR-001-architecture-hexagonale.md) | Architecture hexagonale (Ports & Adapters) | Accepté |
| [ADR-002](ADR-002-spring-modulith.md) | Spring Modulith pour l'isolation des modules | Accepté |
| [ADR-003](ADR-003-communication-inter-modules.md) | Communication inter-modules via API publique | Accepté |
| [ADR-004](ADR-004-jwt-maison-sans-refresh.md) | JWT maison sans refresh ni révocation | Accepté* |
| [ADR-005](ADR-005-redis-cache-et-rate-limiting.md) | Redis pour les compteurs et le rate limiting | Accepté |
| [ADR-006](ADR-006-virtual-threads.md) | Virtual threads Java 25 | Accepté |
| [ADR-007](ADR-007-validation-fichiers-magic-bytes.md) | Validation des fichiers par magic bytes | Accepté |
| [ADR-008](ADR-008-interface-token-issuer.md) | Interface TokenIssuer (DIP) | Accepté |
| [ADR-009](ADR-009-proscore-formula.md) | Formule de calcul du proScore | Accepté |
| [ADR-010](ADR-010-graphe-social-differe.md) | Base de données graphe — décision différée | Différé |

\* ADR-004 : réévaluation recommandée avant le passage en production publique (absence de révocation).