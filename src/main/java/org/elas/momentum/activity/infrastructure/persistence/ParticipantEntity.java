package org.elas.momentum.activity.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "activity_participants", schema = "activities")
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityEntity activity;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ActivityEntity getActivity() { return activity; }
    public void setActivity(ActivityEntity activity) { this.activity = activity; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}
