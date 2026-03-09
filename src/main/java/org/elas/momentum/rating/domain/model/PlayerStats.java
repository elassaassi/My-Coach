package org.elas.momentum.rating.domain.model;

import java.time.Instant;

// Read model - no Spring annotations
public record PlayerStats(
        String userId, String sport, int totalRatings,
        double avgBehavior, double avgTechnicality, double avgTeamwork,
        double winRate, int manOfMatchCount, int proScore, Instant updatedAt
) {
    public static PlayerStats empty(String userId, String sport) {
        return new PlayerStats(userId, sport, 0, 0, 0, 0, 0, 0, 0, Instant.now());
    }

    public PlayerStats withUpdatedRating(double newAvgBehavior, double newAvgTechnicality,
                                          double newAvgTeamwork, int newTotal, int newManOfMatch) {
        int score = calculateProScore(newAvgBehavior, newAvgTechnicality, newAvgTeamwork, newManOfMatch, newTotal);
        return new PlayerStats(userId, sport, newTotal, newAvgBehavior, newAvgTechnicality,
                newAvgTeamwork, winRate, newManOfMatch, score, Instant.now());
    }

    private static int calculateProScore(double behavior, double technicality, double teamwork,
                                          int manOfMatch, int total) {
        if (total == 0) return 0;
        double base = (behavior * 0.3 + technicality * 0.5 + teamwork * 0.2) / 5.0 * 70;
        double bonus = Math.min(manOfMatch * 5.0, 30);
        return (int) Math.min(100, Math.round(base + bonus));
    }
}
