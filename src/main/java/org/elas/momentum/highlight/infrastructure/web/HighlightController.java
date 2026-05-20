package org.elas.momentum.highlight.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.elas.momentum.highlight.application.dto.CommentResponse;
import org.elas.momentum.highlight.application.dto.HighlightResponse;
import org.elas.momentum.highlight.domain.model.MediaType;
import org.elas.momentum.highlight.domain.port.in.AddCommentUseCase;
import org.elas.momentum.highlight.domain.port.in.ArchiveHighlightUseCase;
import org.elas.momentum.highlight.domain.port.in.DeleteHighlightUseCase;
import org.elas.momentum.highlight.domain.port.in.GetCommentsUseCase;
import org.elas.momentum.highlight.domain.port.in.GetHighlightOfDayUseCase;
import org.elas.momentum.highlight.domain.port.in.GetHighlightUseCase;
import org.elas.momentum.highlight.domain.port.in.LikeHighlightUseCase;
import org.elas.momentum.highlight.domain.port.in.PublishHighlightUseCase;
import org.elas.momentum.highlight.domain.port.in.UpdateHighlightUseCase;
import org.elas.momentum.shared.FileValidator;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/highlights")
@Tag(name = "Highlights", description = "Highlights sportifs du jour")
public class HighlightController {

    private final PublishHighlightUseCase  publishHighlightUseCase;
    private final LikeHighlightUseCase     likeHighlightUseCase;
    private final GetHighlightUseCase      getHighlightUseCase;
    private final GetHighlightOfDayUseCase getHighlightOfDayUseCase;
    private final DeleteHighlightUseCase   deleteHighlightUseCase;
    private final UpdateHighlightUseCase   updateHighlightUseCase;
    private final ArchiveHighlightUseCase  archiveHighlightUseCase;
    private final AddCommentUseCase        addCommentUseCase;
    private final GetCommentsUseCase       getCommentsUseCase;

    @Value("${momentum.upload.dir:./uploads}")
    private String uploadDir;

    public HighlightController(PublishHighlightUseCase publishHighlightUseCase,
                               LikeHighlightUseCase likeHighlightUseCase,
                               GetHighlightUseCase getHighlightUseCase,
                               GetHighlightOfDayUseCase getHighlightOfDayUseCase,
                               DeleteHighlightUseCase deleteHighlightUseCase,
                               UpdateHighlightUseCase updateHighlightUseCase,
                               ArchiveHighlightUseCase archiveHighlightUseCase,
                               AddCommentUseCase addCommentUseCase,
                               GetCommentsUseCase getCommentsUseCase) {
        this.publishHighlightUseCase  = publishHighlightUseCase;
        this.likeHighlightUseCase     = likeHighlightUseCase;
        this.getHighlightUseCase      = getHighlightUseCase;
        this.getHighlightOfDayUseCase = getHighlightOfDayUseCase;
        this.deleteHighlightUseCase   = deleteHighlightUseCase;
        this.updateHighlightUseCase   = updateHighlightUseCase;
        this.archiveHighlightUseCase  = archiveHighlightUseCase;
        this.addCommentUseCase        = addCommentUseCase;
        this.getCommentsUseCase       = getCommentsUseCase;
    }

    @GetMapping
    @Operation(summary = "Feed des highlights (triés par likes et récence)")
    public ResponseEntity<ApiResponse<List<HighlightResponse>>> getFeed(
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(getHighlightUseCase.getFeed(Math.min(limit, 100))));
    }

    @PostMapping
    @Operation(summary = "Publier un highlight (photo ou vidéo)")
    public ResponseEntity<ApiResponse<Map<String, String>>> publishHighlight(
            @AuthenticationPrincipal String userId,
            @RequestBody PublishRequest request) {

        var command = new PublishHighlightUseCase.Command(
                userId,
                request.mediaUrl(),
                request.mediaType(),
                request.caption(),
                request.sport()
        );
        String id = publishHighlightUseCase.publish(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("id", id)));
    }

    @PostMapping("/{highlightId}/like")
    @Operation(summary = "Liker ou unliker un highlight")
    public ResponseEntity<ApiResponse<Void>> likeHighlight(
            @PathVariable String highlightId,
            @AuthenticationPrincipal String userId,
            @RequestBody LikeRequest request) {

        likeHighlightUseCase.like(new LikeHighlightUseCase.Like(highlightId, userId, request.liked()));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/today")
    @Operation(summary = "Highlight du jour")
    public ResponseEntity<ApiResponse<HighlightResponse>> getHighlightOfDay() {
        var highlight = getHighlightOfDayUseCase.getTodayHighlight()
                .map(HighlightResponse::from)
                .orElse(null);
        return ResponseEntity.ok(ApiResponse.ok(highlight));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un highlight")
    public ResponseEntity<ApiResponse<HighlightResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(getHighlightUseCase.getById(id)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modifier la légende ou le sport d'un highlight")
    public ResponseEntity<ApiResponse<Void>> updateHighlight(
            @PathVariable String id,
            @AuthenticationPrincipal String userId,
            Authentication authentication,
            @RequestBody UpdateRequest request) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        updateHighlightUseCase.update(new UpdateHighlightUseCase.Command(id, userId, isAdmin,
                request.caption(), request.sport()));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archiver ou désarchiver un highlight")
    public ResponseEntity<ApiResponse<Void>> archiveHighlight(
            @PathVariable String id,
            @AuthenticationPrincipal String userId,
            Authentication authentication,
            @RequestBody ArchiveRequest request) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        archiveHighlightUseCase.archive(new ArchiveHighlightUseCase.Command(id, userId, isAdmin,
                request.archive()));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/archived")
    @Operation(summary = "Publications archivées de l'utilisateur connecté")
    public ResponseEntity<ApiResponse<List<HighlightResponse>>> getArchived(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(getHighlightUseCase.getArchivedByUser(userId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un highlight (créateur ou administrateur)")
    public ResponseEntity<ApiResponse<Void>> deleteHighlight(
            @PathVariable String id,
            @AuthenticationPrincipal String userId,
            Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        deleteHighlightUseCase.delete(id, userId, isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{highlightId}/comments")
    @Operation(summary = "Ajouter un commentaire")
    public ResponseEntity<ApiResponse<Map<String, String>>> addComment(
            @PathVariable String highlightId,
            @AuthenticationPrincipal String userId,
            @RequestBody CommentRequest request) {
        String commentId = addCommentUseCase.addComment(
                new AddCommentUseCase.Command(highlightId, userId, request.content()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("id", commentId)));
    }

    @GetMapping("/{highlightId}/comments")
    @Operation(summary = "Commentaires d'un highlight")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable String highlightId) {
        var comments = getCommentsUseCase.getComments(highlightId).stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(comments));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "Upload une photo ou vidéo pour un highlight")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadMedia(
            @RequestPart("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("EMPTY_FILE", "Le fichier est vide"));
        }

        byte[] magic = readMagicBytes(file.getInputStream());
        boolean isImage = FileValidator.isAllowedImage(magic);
        boolean isVideo = !isImage && FileValidator.isAllowedVideo(magic);

        if (!isImage && !isVideo) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_FILE_TYPE", "Format non autorisé. Acceptés : JPG, PNG, WEBP, GIF, MP4, WebM, AVI"));
        }

        Path dir = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        if (isVideo) {
            // Extension déterminée par magic bytes — jamais par le nom de fichier client.
            String safeExt  = FileValidator.videoExtension(magic);
            String finalName = UUID.randomUUID() + ".mp4";
            Path   tmpPath   = dir.resolve(UUID.randomUUID() + safeExt);
            file.transferTo(tmpPath);
            try {
                Path outPath = dir.resolve(finalName);
                int  exit    = new ProcessBuilder(
                        "ffmpeg", "-y", "-i", tmpPath.toString(),
                        "-c:v", "libx264", "-c:a", "aac",
                        "-movflags", "+faststart",
                        outPath.toString())
                        .redirectErrorStream(true)
                        .start()
                        .waitFor();
                Files.deleteIfExists(tmpPath);
                if (exit == 0) {
                    return ResponseEntity.ok(ApiResponse.ok(Map.of("url", "/uploads/" + finalName)));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored) { /* ffmpeg absent — fallback to original */ }
            String fallbackName = UUID.randomUUID() + safeExt;
            Files.move(tmpPath, dir.resolve(fallbackName));
            return ResponseEntity.ok(ApiResponse.ok(Map.of("url", "/uploads/" + fallbackName)));
        }

        // Image: extension déterminée par magic bytes.
        String filename = UUID.randomUUID() + FileValidator.imageExtension(magic);
        file.transferTo(dir.resolve(filename));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", "/uploads/" + filename)));
    }

    private static byte[] readMagicBytes(InputStream is) throws IOException {
        try (is) {
            return is.readNBytes(FileValidator.MAGIC_BYTES_LENGTH);
        }
    }

    // ── Request records ───────────────────────────────────────────────────────

    record PublishRequest(String mediaUrl, MediaType mediaType, String caption, String sport) {}
    record LikeRequest(boolean liked) {}
    record CommentRequest(String content) {}
    record UpdateRequest(String caption, String sport) {}
    record ArchiveRequest(boolean archive) {}
}
