package org.elas.momentum.rating.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.elas.momentum.rating.domain.model.PlayerLevel;
import org.elas.momentum.rating.domain.port.in.GetLeaderboardUseCase;
import org.elas.momentum.rating.domain.port.in.GetPlayerStatsUseCase;
import org.elas.momentum.rating.domain.port.in.RatePlayerUseCase;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@Tag(name = "Rating", description = "Player rating and leaderboard")
public class RatingController {

    private final RatePlayerUseCase ratePlayerUseCase;
    private final GetPlayerStatsUseCase getStatsUseCase;
    private final GetLeaderboardUseCase getLeaderboardUseCase;

    public RatingController(RatePlayerUseCase ratePlayerUseCase,
                             GetPlayerStatsUseCase getStatsUseCase,
                             GetLeaderboardUseCase getLeaderboardUseCase) {
        this.ratePlayerUseCase = ratePlayerUseCase;
        this.getStatsUseCase = getStatsUseCase;
        this.getLeaderboardUseCase = getLeaderboardUseCase;
    }

    public record RateRequest(
            @NotBlank String activityId,
            @NotBlank String ratedUserId,
            @Min(1) @Max(5) int behavior,
            @Min(1) @Max(5) int technicality,
            @Min(1) @Max(5) int teamwork,
            @NotNull PlayerLevel level,
            boolean isManOfMatch,
            String comment
    ) {}

    @PostMapping
    @Operation(summary = "Rate a player after an activity")
    public ResponseEntity<ApiResponse<String>> ratePlayer(
            @AuthenticationPrincipal String raterId,
            @Valid @RequestBody RateRequest req) {
        var id = ratePlayerUseCase.rate(new RatePlayerUseCase.Command(
                req.activityId(), raterId, req.ratedUserId(),
                req.behavior(), req.technicality(), req.teamwork(),
                req.level(), req.isManOfMatch(), req.comment()));
        return ResponseEntity.ok(ApiResponse.ok(id));
    }

    @GetMapping("/stats/{userId}")
    @Operation(summary = "Get player statistics")
    public ResponseEntity<ApiResponse<?>> getStats(
            @PathVariable String userId,
            @RequestParam(defaultValue = "all") String sport) {
        return ResponseEntity.ok(ApiResponse.ok(getStatsUseCase.getStats(userId, sport)));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard by sport")
    public ResponseEntity<ApiResponse<?>> getLeaderboard(
            @RequestParam String sport,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(
                getLeaderboardUseCase.getLeaderboard(sport, Math.min(limit, 100))));
    }

    @GetMapping("/leaderboard/multi")
    @Operation(summary = "Get multi-sport leaderboard in parallel")
    public ResponseEntity<ApiResponse<?>> getMultiSportLeaderboard(
            @RequestParam List<String> sports,
            @RequestParam(defaultValue = "20") int limitPerSport) {
        return ResponseEntity.ok(ApiResponse.ok(
                getLeaderboardUseCase.getMultiSportLeaderboard(sports, limitPerSport)));
    }
}
