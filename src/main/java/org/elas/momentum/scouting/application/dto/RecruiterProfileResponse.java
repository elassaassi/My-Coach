package org.elas.momentum.scouting.application.dto;

import org.elas.momentum.scouting.domain.model.RecruiterProfile;

import java.time.Instant;
import java.util.List;

public record RecruiterProfileResponse(
        String id,
        String userId,
        String organization,
        List<String> targetSports,
        String targetLevel,
        Instant createdAt
) {
    public static RecruiterProfileResponse from(RecruiterProfile r) {
        return new RecruiterProfileResponse(
                r.getId().value(),
                r.getUserId(),
                r.getOrganization(),
                r.getTargetSports(),
                r.getTargetLevel(),
                r.getCreatedAt()
        );
    }
}
