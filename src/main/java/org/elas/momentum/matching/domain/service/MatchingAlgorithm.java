package org.elas.momentum.matching.domain.service;

import org.elas.momentum.matching.domain.model.MatchCriteria;
import org.elas.momentum.matching.domain.model.MatchOutcome;
import org.elas.momentum.matching.domain.port.out.UserProfilePort;
import org.elas.momentum.matching.domain.port.out.UserProfilePort.CandidateProfile;
import org.elas.momentum.shared.domain.GeoUtils;

import java.util.Comparator;
import java.util.List;

/**
 * Phase 0 — Algorithme de matching basé sur des règles.
 * Aucune dépendance Spring. Testable en <50ms.
 *
 * Score = sport(50) + niveau(30) + distance(20)
 */
public class MatchingAlgorithm {

    private static final int SCORE_SPORT_MATCH    = 50;
    private static final int SCORE_LEVEL_EXACT    = 30;
    private static final int SCORE_LEVEL_ADJACENT = 15;
    private static final int SCORE_DISTANCE_5KM   = 20;
    private static final int SCORE_DISTANCE_10KM  = 10;
    private static final int SCORE_DISTANCE_30KM  = 5;

    public MatchOutcome findBestMatch(MatchCriteria criteria, List<CandidateProfile> candidates) {
        if (candidates.isEmpty()) {
            return new MatchOutcome.NoMatch("Aucun utilisateur pratique ce sport");
        }

        record ScoredCandidate(CandidateProfile profile, double score, double distanceKm) {}

        var scoredCandidates = candidates.stream()
                .filter(c -> c.sport().equalsIgnoreCase(criteria.sport()))
                .map(c -> {
                    double distance = GeoUtils.haversineKm(
                            criteria.latitude(), criteria.longitude(),
                            c.latitude(), c.longitude());
                    if (distance > criteria.maxDistanceKm()) return null;
                    double score = computeScore(criteria, c, distance);
                    return new ScoredCandidate(c, score, distance);
                })
                .filter(sc -> sc != null && sc.score() > 0)
                .sorted(Comparator.comparingDouble(ScoredCandidate::score).reversed())
                .toList();

        if (scoredCandidates.isEmpty()) {
            return new MatchOutcome.NoMatch(
                    "Aucun sportif compatible dans un rayon de " + criteria.maxDistanceKm() + "km");
        }

        var best = scoredCandidates.getFirst();
        return new MatchOutcome.Found(best.profile().userId(), best.score(), best.distanceKm());
    }

    private double computeScore(MatchCriteria criteria, CandidateProfile candidate, double distanceKm) {
        double score = SCORE_SPORT_MATCH;

        // Compatibilité de niveau
        int levelDiff = Math.abs(proficiencyOrdinal(criteria.proficiency())
                - proficiencyOrdinal(candidate.proficiency()));

        score += switch (levelDiff) {
            case 0 -> SCORE_LEVEL_EXACT;
            case 1 -> SCORE_LEVEL_ADJACENT;
            default -> 0;
        };

        // Score de proximité
        if (distanceKm <= 5)       score += SCORE_DISTANCE_5KM;
        else if (distanceKm <= 10) score += SCORE_DISTANCE_10KM;
        else if (distanceKm <= 30) score += SCORE_DISTANCE_30KM;

        return score;
    }

    private int proficiencyOrdinal(String proficiency) {
        return switch (proficiency.toUpperCase()) {
            case "BEGINNER"     -> 0;
            case "INTERMEDIATE" -> 1;
            case "ADVANCED"     -> 2;
            case "ELITE"        -> 3;
            default -> -1;
        };
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        return GeoUtils.haversineKm(lat1, lon1, lat2, lon2);
    }
}
