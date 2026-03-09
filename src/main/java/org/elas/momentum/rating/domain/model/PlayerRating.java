package org.elas.momentum.rating.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root — Rating Bounded Context.
 * No Spring annotations.
 */
public class PlayerRating {

    private final RatingId id;
    private final String activityId;
    private final String raterId;
    private final String ratedUserId;
    private final int behavior;
    private final int technicality;
    private final int teamwork;
    private final PlayerLevel level;
    private final boolean isManOfMatch;
    private final String comment;
    private final Instant createdAt;

    // ── Factory : creation ──────────────────────────────────────────────────────

    public static PlayerRating create(String activityId, String raterId, String ratedUserId,
                                      int behavior, int technicality, int teamwork,
                                      PlayerLevel level, boolean isManOfMatch, String comment) {
        Objects.requireNonNull(activityId, "activityId must not be null");
        Objects.requireNonNull(raterId, "raterId must not be null");
        Objects.requireNonNull(ratedUserId, "ratedUserId must not be null");
        Objects.requireNonNull(level, "level must not be null");
        validateScore("behavior", behavior);
        validateScore("technicality", technicality);
        validateScore("teamwork", teamwork);

        return new PlayerRating(
                RatingId.generate(), activityId, raterId, ratedUserId,
                behavior, technicality, teamwork, level, isManOfMatch, comment, Instant.now()
        );
    }

    // ── Factory : reconstitution from persistence ────────────────────────────────

    public static PlayerRating reconstitute(RatingId id, String activityId, String raterId,
                                            String ratedUserId, int behavior, int technicality,
                                            int teamwork, PlayerLevel level, boolean isManOfMatch,
                                            String comment, Instant createdAt) {
        return new PlayerRating(id, activityId, raterId, ratedUserId,
                behavior, technicality, teamwork, level, isManOfMatch, comment, createdAt);
    }

    // ── Private constructor ─────────────────────────────────────────────────────

    private PlayerRating(RatingId id, String activityId, String raterId, String ratedUserId,
                         int behavior, int technicality, int teamwork, PlayerLevel level,
                         boolean isManOfMatch, String comment, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.activityId = activityId;
        this.raterId = raterId;
        this.ratedUserId = ratedUserId;
        this.behavior = behavior;
        this.technicality = technicality;
        this.teamwork = teamwork;
        this.level = level;
        this.isManOfMatch = isManOfMatch;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // ── Validation ──────────────────────────────────────────────────────────────

    private static void validateScore(String name, int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException(name + " must be between 1 and 5, was: " + value);
        }
    }

    // ── Getters ─────────────────────────────────────────────────────────────────

    public RatingId getId() { return id; }
    public String getActivityId() { return activityId; }
    public String getRaterId() { return raterId; }
    public String getRatedUserId() { return ratedUserId; }
    public int getBehavior() { return behavior; }
    public int getTechnicality() { return technicality; }
    public int getTeamwork() { return teamwork; }
    public PlayerLevel getLevel() { return level; }
    public boolean isManOfMatch() { return isManOfMatch; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
}
