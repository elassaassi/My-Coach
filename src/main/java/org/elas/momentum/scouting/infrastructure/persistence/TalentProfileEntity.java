package org.elas.momentum.scouting.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "talent_profiles", schema = "scouting")
class TalentProfileEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "sport", nullable = false, length = 100)
    private String sport;

    @Column(name = "pro_score", nullable = false)
    private int proScore;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    @Column(name = "recruitment_status", nullable = false, length = 30)
    private String recruitmentStatus;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }
    public int getProScore() { return proScore; }
    public void setProScore(int proScore) { this.proScore = proScore; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
    public String getRecruitmentStatus() { return recruitmentStatus; }
    public void setRecruitmentStatus(String recruitmentStatus) { this.recruitmentStatus = recruitmentStatus; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
