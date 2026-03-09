package org.elas.momentum.activity.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.application.dto.CreateActivityCommand;
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
    private final JoinActivityUseCase joinActivityUseCase;
    private final GetActivityUseCase getActivityUseCase;

    public ActivityController(CreateActivityUseCase createActivityUseCase,
                              JoinActivityUseCase joinActivityUseCase,
                              GetActivityUseCase getActivityUseCase) {
        this.createActivityUseCase = createActivityUseCase;
        this.joinActivityUseCase = joinActivityUseCase;
        this.getActivityUseCase = getActivityUseCase;
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

    @GetMapping("/search")
    @Operation(summary = "Rechercher des sessions ouvertes par sport et localisation")
    public ResponseEntity<ApiResponse<List<ActivityResult>>> search(
            @RequestParam String sport,
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "20") int radiusKm) {

        return ResponseEntity.ok(ApiResponse.ok(
                getActivityUseCase.getOpenBySport(sport, lat, lon, radiusKm)));
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
}
