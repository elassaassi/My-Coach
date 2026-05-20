# ADR-008 : Interface TokenIssuer (principe d'inversion de dépendance)

## Statut
Accepté

## Contexte
`AuthController`, `UserController` et `OAuth2AuthenticationSuccessHandler` injectaient directement `JwtTokenProvider` (classe concrète d'infrastructure). Cette dépendance couplait des composants applicatifs à un détail d'implémentation, rendant difficile le remplacement par Keycloak ou un autre mécanisme d'émission de token.

## Décision
Introduction de l'interface `org.elas.momentum.config.TokenIssuer` :

```java
public interface TokenIssuer {
    String generateToken(String userId, String email, String role);
}
```

`JwtTokenProvider` implémente `TokenIssuer`. Les trois consommateurs injectent `TokenIssuer`.

`JwtAuthenticationFilter`, qui valide les tokens (et non les émet), continue d'injecter `JwtTokenProvider` directement car la validation est une responsabilité interne à l'infrastructure de sécurité.

## Conséquences
**Positif**
- Remplacer l'émission JWT par Keycloak, PASETO ou autre ne nécessite qu'une nouvelle implémentation de `TokenIssuer`.
- Les tests peuvent fournir un `TokenIssuer` stub sans démarrer la logique HMAC.

**Négatif**
- Interface minimale : si de nouvelles méthodes sont nécessaires (ex. `generateRefreshToken`), l'interface devra évoluer avec ses implémentations.