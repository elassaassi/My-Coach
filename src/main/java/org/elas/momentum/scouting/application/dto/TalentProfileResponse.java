package org.elas.momentum.scouting.application.dto;

import org.elas.momentum.scouting.domain.model.RecruitmentStatus;
import org.elas.momentum.scouting.domain.model.TalentProfile;

import java.time.Instant;

public record TalentProfileResponse(
        String userId,
        String sport,
        int proScore,
        boolean isVisible,
        RecruitmentStatus recruitmentStatus,
        Instant updatedAt
) {
    public static TalentProfileResponse from(TalentProfile t) {
        return new TalentProfileResponse(
                t.getUserId().value(),
                t.getSport(),
                t.getProScore(),
                t.isVisible(),
                t.getRecruitmentStatus(),
                t.getUpdatedAt()
        );
    }
}
