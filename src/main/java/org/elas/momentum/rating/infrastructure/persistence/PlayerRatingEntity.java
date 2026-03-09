package org.elas.momentum.rating.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "player_ratings", schema = "rating")
public class PlayerRatingEntity {

    @Id
    private String id;

    @Column(name = "activity_id", nullable = false)
    private String activityId;

    @Column(name = "rater_id", nullable = false)
    private String raterId;

    @Column(name = "rated_user_id", nullable = false)
    private String ratedUserId;

    @Column(nullable = false)
    private int behavior;

    @Column(nullable = false)
    private int technicality;

    @Column(nullable = false)
    private int teamwork;

    @Column(nullable = false)
    private String level;

    @Column(name = "is_man_of_match", nullable = false)
    private boolean isManOfMatch;

    @Column(length = 500)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public PlayerRatingEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }
    public String getRaterId() { return raterId; }
    public void setRaterId(String raterId) { this.raterId = raterId; }
    public String getRatedUserId() { return ratedUserId; }
    public void setRatedUserId(String ratedUserId) { this.ratedUserId = ratedUserId; }
    public int getBehavior() { return behavior; }
    public void setBehavior(int behavior) { this.behavior = behavior; }
    public int getTechnicality() { return technicality; }
    public void setTechnicality(int technicality) { this.technicality = technicality; }
    public int getTeamwork() { return teamwork; }
    public void setTeamwork(int teamwork) { this.teamwork = teamwork; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public boolean isManOfMatch() { return isManOfMatch; }
    public void setManOfMatch(boolean manOfMatch) { isManOfMatch = manOfMatch; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
