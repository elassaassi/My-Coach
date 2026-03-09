package org.elas.momentum.scouting.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root — TalentProfile in the Scouting Bounded Context.
 * userId is the same UUID as the User's id.
 */
public class TalentProfile {

    private final TalentId userId;
    private String sport;
    private int proScore;
    private boolean isVisible;
    private RecruitmentStatus recruitmentStatus;
    private Instant updatedAt;

    // ── Factory : création ────────────────────────────────────────────────────

    public static TalentProfile create(String userId, String sport, int proScore) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(sport, "sport must not be null");
        validateProScore(proScore);

        return new TalentProfile(
                TalentId.of(userId),
                sport,
                proScore,
                true,
                RecruitmentStatus.OPEN,
                Instant.now()
        );
    }

    // ── Factory : reconstitution depuis persistance ────────────────────────────

    public static TalentProfile reconstitute(String userId, String sport, int proScore,
                                             boolean isVisible, RecruitmentStatus recruitmentStatus,
                                             Instant updatedAt) {
        return new TalentProfile(TalentId.of(userId), sport, proScore,
                isVisible, recruitmentStatus, updatedAt);
    }

    // ── Constructor interne ───────────────────────────────────────────────────

    private TalentProfile(TalentId userId, String sport, int proScore,
                          boolean isVisible, RecruitmentStatus recruitmentStatus,
                          Instant updatedAt) {
        this.userId = userId;
        this.sport = sport;
        this.proScore = proScore;
        this.isVisible = isVisible;
        this.recruitmentStatus = recruitmentStatus;
        this.updatedAt = updatedAt;
    }

    // ── Business methods ─────────────────────────────────────────────────────

    public void updateScore(int newScore) {
        validateProScore(newScore);
        this.proScore = newScore;
        this.updatedAt = Instant.now();
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public TalentId getUserId() { return userId; }
    public String getSport() { return sport; }
    public int getProScore() { return proScore; }
    public boolean isVisible() { return isVisible; }
    public RecruitmentStatus getRecruitmentStatus() { return recruitmentStatus; }
    public Instant getUpdatedAt() { return updatedAt; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void validateProScore(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("proScore must be between 0 and 100, got: " + score);
        }
    }
}
