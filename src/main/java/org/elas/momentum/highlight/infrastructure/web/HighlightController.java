package org.elas.momentum.highlight.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.elas.momentum.highlight.application.dto.CommentResponse;
import org.elas.momentum.highlight.application.dto.HighlightResponse;
import org.elas.momentum.highlight.domain.model.MediaType;
import org.elas.momentum.highlight.domain.port.in.AddCommentUseCase;
import org.elas.momentum.highlight.domain.port.in.DeleteHighlightUseCase;
import org.elas.momentum.highlight.domain.port.in.GetCommentsUseCase;
import org.elas.momentum.highlight.domain.port.in.GetHighlightOfDayUseCase;
import org.elas.momentum.highlight.domain.port.in.LikeHighlightUseCase;
import org.elas.momentum.highlight.domain.port.in.PublishHighlightUseCase;
import org.elas.momentum.highlight.domain.port.out.CommentRepository;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/highlights")
@Tag(name = "Highlights", description = "Highlights sportifs du jour")
public class HighlightController {

    private final PublishHighlightUseCase  publishHighlightUseCase;
    private final LikeHighlightUseCase     likeHighlightUseCase;
    private final GetHighlightOfDayUseCase getHighlightOfDayUseCase;
    private final DeleteHighlightUseCase   deleteHighlightUseCase;
    private final AddCommentUseCase        addCommentUseCase;
    private final GetCommentsUseCase       getCommentsUseCase;
    private final HighlightRepository      highlightRepository;
    private final CommentRepository        commentRepository;

    @Value("${momentum.upload.dir:./uploads}")
    private String uploadDir;

    public HighlightController(PublishHighlightUseCase publishHighlightUseCase,
                               LikeHighlightUseCase likeHighlightUseCase,
                               GetHighlightOfDayUseCase getHighlightOfDayUseCase,
                               DeleteHighlightUseCase deleteHighlightUseCase,
                               AddCommentUseCase addCommentUseCase,
                               GetCommentsUseCase getCommentsUseCase,
                               HighlightRepository highlightRepository,
                               CommentRepository commentRepository) {
        this.publishHighlightUseCase  = publishHighlightUseCase;
        this.likeHighlightUseCase     = likeHighlightUseCase;
        this.getHighlightOfDayUseCase = getHighlightOfDayUseCase;
        this.deleteHighlightUseCase   = deleteHighlightUseCase;
        this.addCommentUseCase        = addCommentUseCase;
        this.getCommentsUseCase       = getCommentsUseCase;
        this.highlightRepository      = highlightRepository;
        this.commentRepository        = commentRepository;
    }

    @GetMapping
    @Operation(summary = "Feed des highlights (triés par likes et récence)")
    public ResponseEntity<ApiResponse<List<HighlightResponse>>> getFeed(
            @RequestParam(defaultValue = "30") int limit) {
        var highlights = highlightRepository.findTopByLikesAndRecency(Math.min(limit, 100));
        var ids = highlights.stream().map(h -> h.getId().value()).toList();
        var counts = commentRepository.countByHighlightIds(ids);
        var response = highlights.stream()
                .map(h -> HighlightResponse.from(h, counts.getOrDefault(h.getId().value(), 0L).intValue()))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
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
        var highlight = highlightRepository.findById(id)
                .map(HighlightResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Highlight not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok(highlight));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer son propre highlight")
    public ResponseEntity<ApiResponse<Void>> deleteHighlight(
            @PathVariable String id,
            @AuthenticationPrincipal String userId) {
        deleteHighlightUseCase.delete(id, userId);
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

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_FILE_TYPE", "Seules les images et vidéos sont acceptées"));
        }

        String original = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "file";
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID() + ext;

        Path dir = Path.of(uploadDir);
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(filename));

        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", "/uploads/" + filename)));
    }

    // ── Request records ───────────────────────────────────────────────────────

    record PublishRequest(String mediaUrl, MediaType mediaType, String caption, String sport) {}
    record LikeRequest(boolean liked) {}
    record CommentRequest(String content) {}
}
