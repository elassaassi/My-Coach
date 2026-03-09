package org.elas.momentum.rating.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "leaderboard_cache", schema = "rating")
public class LeaderboardCacheEntity {

    @EmbeddedId
    private LeaderboardId id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private int score;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Embeddable
    public static class LeaderboardId implements java.io.Serializable {
        private String sport;
        private int rank;

        public LeaderboardId() {}

        public LeaderboardId(String sport, int rank) {
            this.sport = sport;
            this.rank = rank;
        }

        public String getSport() { return sport; }
        public void setSport(String sport) { this.sport = sport; }
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }

    public LeaderboardCacheEntity() {}

    public LeaderboardId getId() { return id; }
    public void setId(LeaderboardId id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
