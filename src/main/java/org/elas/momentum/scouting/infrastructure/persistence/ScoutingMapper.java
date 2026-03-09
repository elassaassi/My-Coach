package org.elas.momentum.scouting.infrastructure.persistence;

import org.elas.momentum.scouting.domain.model.*;

import java.util.ArrayList;
import java.util.List;

final class ScoutingMapper {

    private ScoutingMapper() {}

    // ── TalentProfile ─────────────────────────────────────────────────────────

    static TalentProfile toDomain(TalentProfileEntity e) {
        return TalentProfile.reconstitute(
                e.getUserId(),
                e.getSport(),
                e.getProScore(),
                e.isVisible(),
                RecruitmentStatus.valueOf(e.getRecruitmentStatus()),
                e.getUpdatedAt()
        );
    }

    static TalentProfileEntity toEntity(TalentProfile t) {
        var e = new TalentProfileEntity();
        e.setUserId(t.getUserId().value());
        e.setSport(t.getSport());
        e.setProScore(t.getProScore());
        e.setVisible(t.isVisible());
        e.setRecruitmentStatus(t.getRecruitmentStatus().name());
        e.setUpdatedAt(t.getUpdatedAt());
        return e;
    }

    // ── RecruiterProfile ──────────────────────────────────────────────────────

    static RecruiterProfile toDomain(RecruiterProfileEntity e) {
        return RecruiterProfile.reconstitute(
                e.getId(),
                e.getUserId(),
                e.getOrganization(),
                e.getTargetSports(),
                e.getTargetLevel(),
                e.getCreatedAt()
        );
    }

    static RecruiterProfileEntity toEntity(RecruiterProfile r) {
        var e = new RecruiterProfileEntity();
        e.setId(r.getId().value());
        e.setUserId(r.getUserId());
        e.setOrganization(r.getOrganization());
        e.setTargetSports(new ArrayList<>(r.getTargetSports()));
        e.setTargetLevel(r.getTargetLevel());
        e.setCreatedAt(r.getCreatedAt());
        return e;
    }

    // ── ScoutingInterest ──────────────────────────────────────────────────────

    static ScoutingInterest toDomain(ScoutingInterestEntity e) {
        return ScoutingInterest.reconstitute(
                e.getId(),
                e.getRecruiterId(),
                e.getTalentId(),
                InterestStatus.valueOf(e.getStatus()),
                e.getNote(),
                e.getCreatedAt()
        );
    }

    static ScoutingInterestEntity toEntity(ScoutingInterest i) {
        var e = new ScoutingInterestEntity();
        e.setId(i.getId().value());
        e.setRecruiterId(i.getRecruiterId());
        e.setTalentId(i.getTalentId());
        e.setStatus(i.getStatus().name());
        e.setNote(i.getNote());
        e.setCreatedAt(i.getCreatedAt());
        return e;
    }
}
