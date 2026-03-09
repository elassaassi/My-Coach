package org.elas.momentum.coaching.application.dto;

import org.elas.momentum.coaching.domain.model.Coach;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CoachResponse(
        String id,
        String userId,
        String bio,
        List<String> sports,
        List<String> certifications,
        BigDecimal hourlyRate,
        String currency,
        boolean isAvailable,
        double averageRating,
        Instant createdAt
) {
    public static CoachResponse from(Coach coach) {
        return new CoachResponse(
                coach.getId().value(),
                coach.getUserId(),
                coach.getBio(),
                coach.getSports(),
                coach.getCertifications(),
                coach.getHourlyRate(),
                coach.getCurrency(),
                coach.isAvailable(),
                coach.getAverageRating(),
                coach.getCreatedAt()
        );
    }
}
