package org.elas.momentum.matching.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root — Matching Bounded Context.
 */
public class MatchRequest {

    private final MatchRequestId id;
    private final String requesterId;
    private final MatchCriteria criteria;
    private MatchStatus status;
    private String matchedUserId;
    private double matchScore;
    private final Instant createdAt;
    private Instant updatedAt;

    // ── Factory ──────────────────────────────────────────────────────────────

    public static MatchRequest create(String requesterId, MatchCriteria criteria) {
        return new MatchRequest(MatchRequestId.generate(), requesterId, criteria);
    }

    public MatchRequest(MatchRequestId id, String requesterId, MatchCriteria criteria) {
        this.id = Objects.requireNonNull(id);
        this.requesterId = Objects.requireNonNull(requesterId);
        this.criteria = Objects.requireNonNull(criteria);
        this.status = MatchStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // ── Business methods ─────────────────────────────────────────────────────

    public void matchFound(String matchedUserId, double score) {
        Objects.requireNonNull(matchedUserId);
        this.matchedUserId = matchedUserId;
        this.matchScore = score;
        this.status = MatchStatus.FOUND;
        this.updatedAt = Instant.now();
    }

    public void noMatchFound() {
        this.status = MatchStatus.NO_MATCH;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status != MatchStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be cancelled");
        }
        this.status = MatchStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public MatchRequestId getId() { return id; }
    public String getRequesterId() { return requesterId; }
    public MatchCriteria getCriteria() { return criteria; }
    public MatchStatus getStatus() { return status; }
    public String getMatchedUserId() { return matchedUserId; }
    public double getMatchScore() { return matchScore; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
