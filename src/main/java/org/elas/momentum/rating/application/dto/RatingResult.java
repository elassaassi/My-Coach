package org.elas.momentum.rating.application.dto;

import org.elas.momentum.rating.domain.model.PlayerLevel;

import java.time.Instant;

public record RatingResult(
        String id,
        String activityId,
        String raterId,
        String ratedUserId,
        int behavior,
        int technicality,
        int teamwork,
        PlayerLevel level,
        boolean isManOfMatch,
        String comment,
        Instant createdAt
) {}
