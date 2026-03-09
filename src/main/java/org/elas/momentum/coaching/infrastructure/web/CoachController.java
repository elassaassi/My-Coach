package org.elas.momentum.coaching.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.coaching.application.dto.BookCoachingRequest;
import org.elas.momentum.coaching.application.dto.CoachResponse;
import org.elas.momentum.coaching.application.dto.CreateOfferRequest;
import org.elas.momentum.coaching.application.dto.RegisterCoachRequest;
import org.elas.momentum.coaching.domain.port.in.BookCoachingUseCase;
import org.elas.momentum.coaching.domain.port.in.CreateOfferUseCase;
import org.elas.momentum.coaching.domain.port.in.RegisterCoachUseCase;
import org.elas.momentum.coaching.domain.port.in.SearchCoachesUseCase;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coaches")
@Tag(name = "Coaching", description = "Gestion des coachs sportifs et réservations")
public class CoachController {

    private final RegisterCoachUseCase registerCoachUseCase;
    private final SearchCoachesUseCase searchCoachesUseCase;
    private final CreateOfferUseCase createOfferUseCase;
    private final BookCoachingUseCase bookCoachingUseCase;

    public CoachController(RegisterCoachUseCase registerCoachUseCase,
                           SearchCoachesUseCase searchCoachesUseCase,
                           CreateOfferUseCase createOfferUseCase,
                           BookCoachingUseCase bookCoachingUseCase) {
        this.registerCoachUseCase = registerCoachUseCase;
        this.searchCoachesUseCase = searchCoachesUseCase;
        this.createOfferUseCase = createOfferUseCase;
        this.bookCoachingUseCase = bookCoachingUseCase;
    }

    @PostMapping
    @Operation(summary = "S'inscrire en tant que coach sportif")
    public ResponseEntity<ApiResponse<Map<String, String>>> registerCoach(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody RegisterCoachRequest request) {

        String coachId = registerCoachUseCase.register(new RegisterCoachUseCase.Command(
                userId,
                request.bio(),
                request.hourlyRate(),
                request.currency()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("coachId", coachId)));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des coachs disponibles")
    public ResponseEntity<ApiResponse<List<CoachResponse>>> searchCoaches(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) Double minRating) {

        List<CoachResponse> coaches = searchCoachesUseCase
                .search(new SearchCoachesUseCase.Query(sport, maxRate, minRating))
                .stream()
                .map(CoachResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(coaches));
    }

    @PostMapping("/{coachId}/offers")
    @Operation(summary = "Créer une offre de coaching")
    public ResponseEntity<ApiResponse<Map<String, String>>> createOffer(
            @PathVariable String coachId,
            @Valid @RequestBody CreateOfferRequest request) {

        String offerId = createOfferUseCase.createOffer(new CreateOfferUseCase.Command(
                coachId,
                request.title(),
                request.description(),
                request.targetAudience(),
                request.sport(),
                request.durationMin(),
                request.price(),
                request.currency()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("offerId", offerId)));
    }

    @PostMapping("/bookings")
    @Operation(summary = "Réserver une session de coaching")
    public ResponseEntity<ApiResponse<Map<String, String>>> bookCoaching(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BookCoachingRequest request) {

        String bookingId = bookCoachingUseCase.book(new BookCoachingUseCase.Command(
                request.offerId(),
                userId,
                request.clientType(),
                request.scheduledAt()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("bookingId", bookingId)));
    }
}
