package org.elas.momentum.scouting.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.elas.momentum.scouting.application.dto.RecruiterProfileResponse;
import org.elas.momentum.scouting.application.dto.TalentProfileResponse;
import org.elas.momentum.scouting.domain.port.in.ExpressInterestUseCase;
import org.elas.momentum.scouting.domain.port.in.GetProCandidatesUseCase;
import org.elas.momentum.scouting.domain.port.in.SearchTalentsUseCase;
import org.elas.momentum.scouting.domain.port.out.RecruiterRepository;
import org.elas.momentum.scouting.domain.model.RecruiterProfile;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scouting")
@Tag(name = "Scouting", description = "Détection et recrutement de talents sportifs")
public class ScoutingController {

    private final SearchTalentsUseCase searchTalentsUseCase;
    private final GetProCandidatesUseCase getProCandidatesUseCase;
    private final ExpressInterestUseCase expressInterestUseCase;
    private final RecruiterRepository recruiterRepository;

    public ScoutingController(SearchTalentsUseCase searchTalentsUseCase,
                              GetProCandidatesUseCase getProCandidatesUseCase,
                              ExpressInterestUseCase expressInterestUseCase,
                              RecruiterRepository recruiterRepository) {
        this.searchTalentsUseCase = searchTalentsUseCase;
        this.getProCandidatesUseCase = getProCandidatesUseCase;
        this.expressInterestUseCase = expressInterestUseCase;
        this.recruiterRepository = recruiterRepository;
    }

    @GetMapping("/talents")
    @Operation(summary = "Rechercher des talents par sport et score minimum")
    public ResponseEntity<ApiResponse<List<TalentProfileResponse>>> searchTalents(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) String region) {

        var query = new SearchTalentsUseCase.Query(sport, minScore, region);
        List<TalentProfileResponse> results = searchTalentsUseCase.search(query).stream()
                .map(TalentProfileResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/talents/pro")
    @Operation(summary = "Obtenir les candidats pro par sport et score minimum (défaut 80)")
    public ResponseEntity<ApiResponse<List<TalentProfileResponse>>> getProCandidates(
            @RequestParam String sport,
            @RequestParam(defaultValue = "80") int minScore) {

        List<TalentProfileResponse> results = getProCandidatesUseCase
                .getProCandidates(sport, minScore).stream()
                .map(TalentProfileResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @PostMapping("/interests")
    @Operation(summary = "Exprimer un intérêt de recrutement pour un talent")
    public ResponseEntity<ApiResponse<Map<String, String>>> expressInterest(
            @AuthenticationPrincipal String userId,
            @RequestBody InterestRequest request) {

        // Recruiter is identified by their userId — look up their recruiter profile id
        RecruiterProfile recruiterProfile = recruiterRepository.findRecruiterByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "No recruiter profile found for user: " + userId));

        var command = new ExpressInterestUseCase.Command(
                recruiterProfile.getId().value(),
                request.talentId(),
                request.note()
        );
        String interestId = expressInterestUseCase.express(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("id", interestId)));
    }

    @PostMapping("/recruiters")
    @Operation(summary = "Enregistrer un profil recruteur")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> registerRecruiter(
            @AuthenticationPrincipal String userId,
            @RequestBody RecruiterRequest request) {

        RecruiterProfile profile = RecruiterProfile.create(
                userId,
                request.organization(),
                request.targetSports(),
                request.targetLevel()
        );
        RecruiterProfile saved = recruiterRepository.save(profile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(RecruiterProfileResponse.from(saved)));
    }

    // ── Request records ───────────────────────────────────────────────────────

    record InterestRequest(String talentId, String note) {}

    record RecruiterRequest(String organization, List<String> targetSports, String targetLevel) {}
}
