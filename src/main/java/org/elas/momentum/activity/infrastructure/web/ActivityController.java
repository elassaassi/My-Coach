package org.elas.momentum.activity.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.activity.application.dto.ActivityMessageResult;
import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.application.dto.CreateActivityCommand;
import org.elas.momentum.activity.application.dto.SendMessageCommand;
import org.elas.momentum.activity.application.usecase.ActivityChatService;
import org.elas.momentum.activity.domain.port.in.CreateActivityUseCase;
import org.elas.momentum.activity.domain.port.in.GetActivityUseCase;
import org.elas.momentum.activity.domain.port.in.JoinActivityUseCase;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@Tag(name = "Activities", description = "Sessions sportives")
public class ActivityController {

    private final CreateActivityUseCase createActivityUseCase;
    private final JoinActivityUseCase   joinActivityUseCase;
    private final GetActivityUseCase    getActivityUseCase;
    private final ActivityChatService   chatService;

    public ActivityController(CreateActivityUseCase createActivityUseCase,
                              JoinActivityUseCase joinActivityUseCase,
                              GetActivityUseCase getActivityUseCase,
                              ActivityChatService chatService) {
        this.createActivityUseCase = createActivityUseCase;
        this.joinActivityUseCase   = joinActivityUseCase;
        this.getActivityUseCase    = getActivityUseCase;
        this.chatService           = chatService;
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle session sportive")
    public ResponseEntity<ApiResponse<ActivityResult>> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateActivityCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createActivityUseCase.create(userId, command)));
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "Détail d'une session")
    public ResponseEntity<ApiResponse<ActivityResult>> getById(@PathVariable String activityId) {
        return ResponseEntity.ok(ApiResponse.ok(getActivityUseCase.getById(activityId)));
    }

    /**
     * Recherche flexible — tous les paramètres sont optionnels.
     * Exemples :
     *   GET /search                          → toutes les activités OPEN (défaut)
     *   GET /search?sport=football           → football OPEN
     *   GET /search?city=Casablanca&status=OPEN
     *   GET /search?sport=tennis&city=Rabat&status=OPEN&page=0&size=20
     */
    @GetMapping("/search")
    @Operation(summary = "Rechercher des sessions (sport, ville, statut)")
    public ResponseEntity<ApiResponse<List<ActivityResult>>> search(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "OPEN") String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.ok(
                getActivityUseCase.search(sport, city, status, page, size)));
    }

    @GetMapping("/me")
    @Operation(summary = "Mes sessions (organisateur + participant)")
    public ResponseEntity<ApiResponse<List<ActivityResult>>> getMyActivities(
            @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(ApiResponse.ok(getActivityUseCase.getByUser(userId)));
    }

    @PostMapping("/{activityId}/join")
    @Operation(summary = "Rejoindre une session")
    public ResponseEntity<ApiResponse<ActivityResult>> join(
            @PathVariable String activityId,
            @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(ApiResponse.ok(joinActivityUseCase.join(activityId, userId)));
    }

    @DeleteMapping("/{activityId}/leave")
    @Operation(summary = "Quitter une session")
    public ResponseEntity<ApiResponse<ActivityResult>> leave(
            @PathVariable String activityId,
            @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(ApiResponse.ok(joinActivityUseCase.leave(activityId, userId)));
    }

    // ── Chat ──────────────────────────────────────────────────────────────────

    @PostMapping("/{activityId}/messages")
    @Operation(summary = "Envoyer un message dans le chat de la session")
    public ResponseEntity<ApiResponse<ActivityMessageResult>> sendMessage(
            @PathVariable String activityId,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody SendMessageCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(chatService.send(activityId, userId, command)));
    }

    @GetMapping("/{activityId}/messages")
    @Operation(summary = "Récupérer les messages du chat (50 derniers)")
    public ResponseEntity<ApiResponse<List<ActivityMessageResult>>> getMessages(
            @PathVariable String activityId,
            @RequestParam(defaultValue = "50") int limit) {

        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(activityId, Math.min(limit, 100))));
    }
}
