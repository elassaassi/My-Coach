package org.elas.momentum.matching.application.dto;

import java.time.Instant;

public record MatchResponse(
        String id,
        String requesterId,
        String sport,
        String proficiency,
        double latitude,
        double longitude,
        int maxDistanceKm,
        String status,
        String matchedUserId,
        double matchScore,
        Instant createdAt
) {}
