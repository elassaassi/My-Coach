package org.elas.momentum.scouting.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruiter_profiles", schema = "scouting")
class RecruiterProfileEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "organization", nullable = false, length = 200)
    private String organization;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "recruiter_sports",
            schema = "scouting",
            joinColumns = @JoinColumn(name = "recruiter_id")
    )
    @Column(name = "sport", length = 100)
    private List<String> targetSports = new ArrayList<>();

    @Column(name = "target_level", nullable = false, length = 30)
    private String targetLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public List<String> getTargetSports() { return targetSports; }
    public void setTargetSports(List<String> targetSports) { this.targetSports = targetSports; }
    public String getTargetLevel() { return targetLevel; }
    public void setTargetLevel(String targetLevel) { this.targetLevel = targetLevel; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
