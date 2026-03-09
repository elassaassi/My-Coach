package org.elas.momentum.rating.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "player_stats", schema = "rating")
public class PlayerStatsEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String sport;

    @Column(name = "total_ratings", nullable = false)
    private int totalRatings;

    @Column(name = "avg_behavior", nullable = false)
    private double avgBehavior;

    @Column(name = "avg_technicality", nullable = false)
    private double avgTechnicality;

    @Column(name = "avg_teamwork", nullable = false)
    private double avgTeamwork;

    @Column(name = "win_rate", nullable = false)
    private double winRate;

    @Column(name = "man_of_match_count", nullable = false)
    private int manOfMatchCount;

    @Column(name = "pro_score", nullable = false)
    private int proScore;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public PlayerStatsEntity() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public double getAvgBehavior() { return avgBehavior; }
    public void setAvgBehavior(double avgBehavior) { this.avgBehavior = avgBehavior; }
    public double getAvgTechnicality() { return avgTechnicality; }
    public void setAvgTechnicality(double avgTechnicality) { this.avgTechnicality = avgTechnicality; }
    public double getAvgTeamwork() { return avgTeamwork; }
    public void setAvgTeamwork(double avgTeamwork) { this.avgTeamwork = avgTeamwork; }
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    public int getManOfMatchCount() { return manOfMatchCount; }
    public void setManOfMatchCount(int manOfMatchCount) { this.manOfMatchCount = manOfMatchCount; }
    public int getProScore() { return proScore; }
    public void setProScore(int proScore) { this.proScore = proScore; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
