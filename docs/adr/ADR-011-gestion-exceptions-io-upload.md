# ADR-011 — Gestion des IOException dans le GlobalExceptionHandler

**Statut** : Accepté  
**Date** : 2026-05-20  
**Auteur** : Youness ELASSAASSI

---

## Contexte

Le `GlobalExceptionHandler` contenait un handler `@ExceptionHandler(IOException.class)` conçu à l'origine pour absorber silencieusement les erreurs "Broken pipe" (client qui se déconnecte en cours de streaming). Il retournait `ResponseEntity.ok().build()` — HTTP 200, corps vide.

Ce handler capturait en réalité **toutes** les sous-classes de `IOException`, y compris les erreurs I/O légitimes (ex. `NoSuchFileException` lors de l'upload vidéo) et retournait systématiquement une réponse vide 200. Côté Angular, `HttpClient` recevait un corps vide, échouait à parser `r.data.url`, et affichait le message de fallback "Erreur lors de l'upload." sans indication du vrai problème.

Bug corrélé dans `HighlightController.uploadMedia()` : `Files.deleteIfExists(tmpPath)` était appelé **avant** le check `if (exit == 0)`, supprimant le fichier temporaire même quand ffmpeg échouait — rendant le fallback `Files.move(tmpPath, ...)` impossible.

---

## Décision

### 1. Scope du handler IOException

Le handler est restreint aux erreurs de déconnexion client (Broken pipe / Connection reset) détectées par message. Toute autre `IOException` est traitée comme une erreur serveur réelle : HTTP 500 + `ApiResponse.error("IO_ERROR", ...)`.

```java
@ExceptionHandler(IOException.class)
public ResponseEntity<ApiResponse<Void>> handleIoException(IOException ex) {
    String msg = ex.getMessage();
    if (msg != null && (msg.contains("Broken pipe") || msg.contains("Connection reset"))) {
        log.debug("Client disconnected: {}", msg);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
    log.error("I/O error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("IO_ERROR", "Une erreur I/O inattendue s'est produite"));
}
```

### 2. Ordre des opérations dans le path vidéo

`Files.deleteIfExists(tmpPath)` est déplacé à l'intérieur du bloc `if (exit == 0)`, laissant le fichier temporaire disponible pour le fallback en cas d'échec ffmpeg.

---

## Conséquences

- Les erreurs I/O réelles (disque plein, permissions, fichier manquant) remontent correctement en HTTP 500 avec un `ApiResponse` structuré — lisible par le frontend.
- Le comportement silencieux pour les déconnexions client ("Broken pipe") est préservé.
- Le fallback vidéo (ffmpeg absent ou non-zero exit) fonctionne correctement : le fichier temporaire est conservé jusqu'au `Files.move()`.
- La couverture du path d'upload est désormais assurée par `HighlightUploadControllerTest` (5 cas : JPEG valide, PNG valide, fichier vide, magic bytes invalides, bytes aléatoires).

---

## Alternatives écartées

| Alternative | Raison du rejet |
|---|---|
| Supprimer le handler `IOException` | Les Broken pipe généreraient des stack traces ERROR en prod pour des événements normaux |
| `try/catch(IOException)` local dans `uploadMedia` | Duplication si d'autres endpoints font des I/O ; le handler global est le bon endroit |
| Retourner HTTP 499 (Client Closed Request) pour Broken pipe | Non standard, pas supporté nativement par Spring — 200 silencieux reste acceptable pour ce cas |