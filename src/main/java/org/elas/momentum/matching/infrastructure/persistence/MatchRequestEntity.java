package org.elas.momentum.matching.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "match_requests", schema = "matching")
public class MatchRequestEntity {

    @Id
    private String id;

    @Column(name = "requester_id", nullable = false)
    private String requesterId;

    @Column(name = "sport", nullable = false)
    private String sport;

    @Column(name = "proficiency", nullable = false)
    private String proficiency;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "max_distance_km", nullable = false)
    private int maxDistanceKm;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "matched_user_id")
    private String matchedUserId;

    @Column(name = "match_score")
    private double matchScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequesterId() { return requesterId; }
    public void setRequesterId(String requesterId) { this.requesterId = requesterId; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getMaxDistanceKm() { return maxDistanceKm; }
    public void setMaxDistanceKm(int maxDistanceKm) { this.maxDistanceKm = maxDistanceKm; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMatchedUserId() { return matchedUserId; }
    public void setMatchedUserId(String matchedUserId) { this.matchedUserId = matchedUserId; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
