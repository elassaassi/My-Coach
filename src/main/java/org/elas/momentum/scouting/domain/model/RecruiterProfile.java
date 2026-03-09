package org.elas.momentum.scouting.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root — RecruiterProfile in the Scouting Bounded Context.
 */
public class RecruiterProfile {

    private final RecruiterId id;
    private final String userId;
    private String organization;
    private List<String> targetSports;
    private String targetLevel;
    private final Instant createdAt;

    // ── Factory : création ────────────────────────────────────────────────────

    public static RecruiterProfile create(String userId, String organization,
                                          List<String> targetSports, String targetLevel) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(organization, "organization must not be null");
        if (organization.isBlank()) {
            throw new IllegalArgumentException("organization must not be blank");
        }
        return new RecruiterProfile(
                RecruiterId.generate(),
                userId,
                organization,
                targetSports != null ? List.copyOf(targetSports) : List.of(),
                targetLevel,
                Instant.now()
        );
    }

    // ── Factory : reconstitution depuis persistance ────────────────────────────

    public static RecruiterProfile reconstitute(String id, String userId, String organization,
                                                List<String> targetSports, String targetLevel,
                                                Instant createdAt) {
        return new RecruiterProfile(RecruiterId.of(id), userId, organization,
                targetSports != null ? List.copyOf(targetSports) : List.of(),
                targetLevel, createdAt);
    }

    // ── Constructor interne ───────────────────────────────────────────────────

    private RecruiterProfile(RecruiterId id, String userId, String organization,
                             List<String> targetSports, String targetLevel, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.organization = organization;
        this.targetSports = targetSports;
        this.targetLevel = targetLevel;
        this.createdAt = createdAt;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public RecruiterId getId() { return id; }
    public String getUserId() { return userId; }
    public String getOrganization() { return organization; }
    public List<String> getTargetSports() { return targetSports; }
    public String getTargetLevel() { return targetLevel; }
    public Instant getCreatedAt() { return createdAt; }
}
