package org.elas.momentum.matching.domain;

import org.elas.momentum.matching.domain.model.MatchCriteria;
import org.elas.momentum.matching.domain.model.MatchOutcome;
import org.elas.momentum.matching.domain.port.out.UserProfilePort;
import org.elas.momentum.matching.domain.service.MatchingAlgorithm;
import org.elas.momentum.shared.domain.GeoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires de l'algorithme de matching — purs Java, <50ms.
 */
class MatchingAlgorithmTest {

    private MatchingAlgorithm algorithm;

    // Casablanca ~33.59°N, -7.61°E
    private static final double CASA_LAT  = 33.5883;
    private static final double CASA_LON  = -7.6114;
    // Rabat ~34.02°N, -6.85°E (distance ~87km de Casa)
    private static final double RABAT_LAT = 34.0209;
    private static final double RABAT_LON = -6.8416;

    @BeforeEach
    void setUp() {
        algorithm = new MatchingAlgorithm();
    }

    @Test
    @DisplayName("Match parfait : même sport, même niveau, < 5km")
    void findBestMatch_perfectMatch_shouldReturnFound() {
        var criteria = MatchCriteria.of("football", "INTERMEDIATE", CASA_LAT, CASA_LON, 50);
        var candidate = new UserProfilePort.CandidateProfile(
                "user-2", "football", "INTERMEDIATE", 3,
                CASA_LAT + 0.01, CASA_LON + 0.01 // ~1km
        );

        MatchOutcome outcome = algorithm.findBestMatch(criteria, List.of(candidate));

        assertThat(outcome).isInstanceOf(MatchOutcome.Found.class);
        MatchOutcome.Found found = (MatchOutcome.Found) outcome;
        assertThat(found.matchedUserId()).isEqualTo("user-2");
        assertThat(found.score()).isGreaterThanOrEqualTo(90.0); // 50 sport + 30 niveau + 20 distance
    }

    @Test
    @DisplayName("Aucun candidat → NoMatch")
    void findBestMatch_noCandidates_shouldReturnNoMatch() {
        var criteria = MatchCriteria.of("tennis", "BEGINNER", CASA_LAT, CASA_LON, 20);
        MatchOutcome outcome = algorithm.findBestMatch(criteria, List.of());
        assertThat(outcome).isInstanceOf(MatchOutcome.NoMatch.class);
    }

    @Test
    @DisplayName("Candidat hors rayon → NoMatch")
    void findBestMatch_candidateTooFar_shouldReturnNoMatch() {
        var criteria = MatchCriteria.of("football", "INTERMEDIATE", CASA_LAT, CASA_LON, 20);
        var candidate = new UserProfilePort.CandidateProfile(
                "user-rabat", "football", "INTERMEDIATE", 3, RABAT_LAT, RABAT_LON
        );

        MatchOutcome outcome = algorithm.findBestMatch(criteria, List.of(candidate));
        assertThat(outcome).isInstanceOf(MatchOutcome.NoMatch.class);
    }

    @Test
    @DisplayName("Meilleur candidat sélectionné parmi plusieurs")
    void findBestMatch_multipleCandidates_shouldSelectBest() {
        var criteria = MatchCriteria.of("football", "INTERMEDIATE", CASA_LAT, CASA_LON, 50);

        var nearBeginner = new UserProfilePort.CandidateProfile(
                "near-beginner", "football", "BEGINNER", 1,
                CASA_LAT + 0.01, CASA_LON // ~1km, niveau adjacent
        );
        var nearIntermediate = new UserProfilePort.CandidateProfile(
                "near-intermediate", "football", "INTERMEDIATE", 3,
                CASA_LAT + 0.01, CASA_LON // ~1km, niveau exact
        );
        var farIntermediate = new UserProfilePort.CandidateProfile(
                "far-intermediate", "football", "INTERMEDIATE", 3,
                CASA_LAT + 0.2, CASA_LON + 0.2 // ~25km
        );

        MatchOutcome outcome = algorithm.findBestMatch(
                criteria, List.of(nearBeginner, farIntermediate, nearIntermediate));

        assertThat(outcome).isInstanceOf(MatchOutcome.Found.class);
        MatchOutcome.Found found = (MatchOutcome.Found) outcome;
        assertThat(found.matchedUserId()).isEqualTo("near-intermediate");
    }

    @Test
    @DisplayName("Niveaux incompatibles (>1 d'écart) → score niveau = 0")
    void findBestMatch_incompatibleLevels_shouldStillMatch_butLowerScore() {
        var criteria = MatchCriteria.of("football", "BEGINNER", CASA_LAT, CASA_LON, 50);
        // ELITE est à 3 niveaux de BEGINNER → pas de score niveau
        var candidate = new UserProfilePort.CandidateProfile(
                "elite", "football", "ELITE", 10,
                CASA_LAT + 0.01, CASA_LON + 0.01
        );

        MatchOutcome outcome = algorithm.findBestMatch(criteria, List.of(candidate));
        // Score = 50 (sport) + 0 (niveau incompatible) + 20 (< 5km) = 70
        assertThat(outcome).isInstanceOf(MatchOutcome.Found.class);
        MatchOutcome.Found found = (MatchOutcome.Found) outcome;
        assertThat(found.score()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("Formule Haversine : distance Casablanca-Rabat ~87km")
    void haversine_casaToRabat_shouldBe87km() {
        double distance = GeoUtils.haversineKm(CASA_LAT, CASA_LON, RABAT_LAT, RABAT_LON);
        assertThat(distance).isBetween(85.0, 90.0);
    }

    @Test
    @DisplayName("describe() retourne le bon message pour chaque outcome")
    void describe_sealedOutcome_shouldMatchPattern() {
        assertThat(MatchOutcome.describe(new MatchOutcome.Found("u1", 95.0, 3.5)))
                .contains("u1").contains("95");
        assertThat(MatchOutcome.describe(new MatchOutcome.NoMatch("raison")))
                .contains("raison");
        assertThat(MatchOutcome.describe(new MatchOutcome.Pending("req-1")))
                .contains("req-1");
    }
}
