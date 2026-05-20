# ADR-007 : Validation des fichiers uploadés par magic bytes

## Statut
Accepté

## Contexte
Les endpoints d'upload (avatar utilisateur, media highlight) acceptent des fichiers depuis des clients non maîtrisés. Valider uniquement le `Content-Type` HTTP ou l'extension du nom de fichier fournis par le client est insuffisant : les deux peuvent être falsifiés.

## Décision
Validation centralisée dans `org.elas.momentum.shared.FileValidator` :
- Lecture des **12 premiers octets** du flux (`MAGIC_BYTES_LENGTH = 12`).
- Détection par signature binaire (magic bytes) : JPEG (`FF D8 FF`), PNG (`89 50 4E 47`), WebP (`RIFF….WEBP`), GIF (`GIF8`), MP4/MOV (`ftyp` à l'offset 4), WebM (`1A 45 DF A3`), AVI (`RIFF….AVI`).
- L'extension du fichier de destination est dérivée des magic bytes, jamais du nom fourni par le client.
- Pour les vidéos, re-encodage via `ffmpeg` vers H.264/AAC avec fallback si `ffmpeg` est absent.

**Règle** : ni `file.getContentType()` ni `file.getOriginalFilename()` ne sont utilisés pour décider du type ou du nom de fichier final.

## Conséquences
**Positif**
- Prévient l'upload déguisé (ex. `.php` renommé en `.jpg`, SVG avec XSS).
- La logique de validation est centralisée et réutilisable par tous les modules.

**Négatif**
- Les 12 octets sont lus une fois puis le flux est consommé : l'InputStream ne peut pas être rembobiné ; l'implémentation doit en tenir compte (lecture séparée via `getInputStream()`).
- `ffmpeg` doit être installé sur l'hôte de déploiement pour le re-encodage vidéo.