package org.elas.momentum.scouting.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "scouting_interests", schema = "scouting")
class ScoutingInterestEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "recruiter_id", nullable = false)
    private String recruiterId;

    @Column(name = "talent_id", nullable = false)
    private String talentId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRecruiterId() { return recruiterId; }
    public void setRecruiterId(String recruiterId) { this.recruiterId = recruiterId; }
    public String getTalentId() { return talentId; }
    public void setTalentId(String talentId) { this.talentId = talentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
