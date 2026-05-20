# ADR-004 : JWT maison sans mécanisme de refresh ni de révocation

## Statut
Accepté — réévaluation recommandée avant le passage en production publique

## Contexte
L'authentification doit être stateless pour supporter les virtual threads et une future scalabilité horizontale. Keycloak est disponible en option (profil `keycloak`) mais ajoute une dépendance opérationnelle lourde en Phase 0/1.

## Décision
- Tokens JWT signés HMAC-SHA256 via **JJWT 0.12.6**, clé configurée via `momentum.jwt.secret`.
- Durée de vie : **24 heures** (`momentum.jwt.expiration-ms`).
- **Pas de refresh token** : à expiration, l'utilisateur se reconnecte.
- **Pas de révocation** : un token valide l'est jusqu'à expiration, même après déconnexion ou suspension.
- La suspension de compte est vérifiée à chaque appel par le use case métier, pas au niveau du token.

## Conséquences
**Positif**
- Aucun état serveur à gérer, déploiement simplifié.
- Compatible avec le profil `keycloak` en production : `KeycloakJwtConverter` remplace `JwtTokenProvider` sans changer les controllers.

**Négatif**
- Un token volé reste valide 24h sans possibilité de révocation immédiate.
- Pas de rotation de clé sans invalider tous les tokens existants.

---

## Plan de remédiation avant production publique

Deux voies, par ordre de complexité croissante :

### Option A — Blacklist Redis (recommandée Phase 2)
Ajouter un `Set<String>` Redis keyed `jwt:blacklist:<jti>` avec TTL = durée restante du token.

```
// À la déconnexion ou suspension de compte :
redis.set("jwt:blacklist:" + jti, "1", Duration.ofMillis(remainingTtl));

// Dans JwtAuthenticationFilter.doFilterInternal() :
if (redisTemplate.hasKey("jwt:blacklist:" + jti)) {
    response.sendError(401, "Token révoqué");
    return;
}
```

**Impact :** modification de `JwtAuthenticationFilter` uniquement. Redis est déjà disponible.  
**Coût :** faible — TTL garantit le nettoyage automatique, pas de fuite mémoire.

### Option B — Basculer sur Keycloak (recommandée production complète)
Le profil `keycloak` est déjà câblé (`KeycloakJwtConverter`). Keycloak gère révocation, refresh tokens, PKCE et rotation de clés nativement.  
**Impact :** opérationnel (déployer Keycloak en HA), zéro changement applicatif côté controllers.

---

**Décision :** Option A en Phase 2 dès que la base d'utilisateurs dépasse 1 000 comptes actifs. Option B pour la mise en production publique à grande échelle.