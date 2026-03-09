package org.elas.momentum.matching.domain.model;

import java.util.Objects;

public record MatchCriteria(
        String sport,
        String proficiency,
        double latitude,
        double longitude,
        int maxDistanceKm
) {
    public MatchCriteria {
        Objects.requireNonNull(sport);
        Objects.requireNonNull(proficiency);
        if (maxDistanceKm <= 0) throw new IllegalArgumentException("maxDistanceKm must be positive");
    }

    public static MatchCriteria of(String sport, String proficiency,
                                   double lat, double lon, int maxDistanceKm) {
        return new MatchCriteria(sport, proficiency, lat, lon, maxDistanceKm);
    }
}
