package org.elas.momentum.matching.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.matching.application.dto.MatchRequestCommand;
import org.elas.momentum.matching.application.dto.MatchResponse;
import org.elas.momentum.matching.domain.port.in.GetMatchUseCase;
import org.elas.momentum.matching.domain.port.in.RequestMatchUseCase;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Matching", description = "Matching sportif entre utilisateurs")
public class MatchController {

    private final RequestMatchUseCase requestMatchUseCase;
    private final GetMatchUseCase getMatchUseCase;

    public MatchController(RequestMatchUseCase requestMatchUseCase,
                           GetMatchUseCase getMatchUseCase) {
        this.requestMatchUseCase = requestMatchUseCase;
        this.getMatchUseCase = getMatchUseCase;
    }

    @PostMapping
    @Operation(summary = "Lancer une recherche de match")
    public ResponseEntity<ApiResponse<MatchResponse>> requestMatch(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody MatchRequestCommand command) {

        MatchResponse response = requestMatchUseCase.requestMatch(userId, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/me")
    @Operation(summary = "Mes demandes de match")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMyMatches(
            @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(ApiResponse.ok(getMatchUseCase.getByUser(userId)));
    }

    @GetMapping("/{matchId}")
    @Operation(summary = "Détail d'une demande de match")
    public ResponseEntity<ApiResponse<MatchResponse>> getById(@PathVariable String matchId) {
        return ResponseEntity.ok(ApiResponse.ok(getMatchUseCase.getById(matchId)));
    }
}
