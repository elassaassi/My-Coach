package org.elas.momentum.scouting.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root — ScoutingInterest in the Scouting Bounded Context.
 */
public class ScoutingInterest {

    private final InterestId id;
    private final String recruiterId;
    private final String talentId;
    private InterestStatus status;
    private String note;
    private final Instant createdAt;

    // ── Factory : création ────────────────────────────────────────────────────

    public static ScoutingInterest create(String recruiterId, String talentId, String note) {
        Objects.requireNonNull(recruiterId, "recruiterId must not be null");
        Objects.requireNonNull(talentId, "talentId must not be null");

        return new ScoutingInterest(
                InterestId.generate(),
                recruiterId,
                talentId,
                InterestStatus.PENDING,
                note,
                Instant.now()
        );
    }

    // ── Factory : reconstitution depuis persistance ────────────────────────────

    public static ScoutingInterest reconstitute(String id, String recruiterId, String talentId,
                                                InterestStatus status, String note,
                                                Instant createdAt) {
        return new ScoutingInterest(InterestId.of(id), recruiterId, talentId,
                status, note, createdAt);
    }

    // ── Constructor interne ───────────────────────────────────────────────────

    private ScoutingInterest(InterestId id, String recruiterId, String talentId,
                             InterestStatus status, String note, Instant createdAt) {
        this.id = id;
        this.recruiterId = recruiterId;
        this.talentId = talentId;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
    }

    // ── Business methods ─────────────────────────────────────────────────────

    public void accept() {
        if (status == InterestStatus.REJECTED) {
            throw new IllegalStateException("Cannot accept an already rejected interest");
        }
        this.status = InterestStatus.ACCEPTED;
    }

    public void reject() {
        if (status == InterestStatus.ACCEPTED) {
            throw new IllegalStateException("Cannot reject an already accepted interest");
        }
        this.status = InterestStatus.REJECTED;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public InterestId getId() { return id; }
    public String getRecruiterId() { return recruiterId; }
    public String getTalentId() { return talentId; }
    public InterestStatus getStatus() { return status; }
    public String getNote() { return note; }
    public Instant getCreatedAt() { return createdAt; }
}
