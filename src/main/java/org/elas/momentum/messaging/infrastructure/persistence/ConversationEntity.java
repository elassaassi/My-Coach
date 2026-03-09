package org.elas.momentum.messaging.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "conversations", schema = "messaging",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant_a", "participant_b"}))
public class ConversationEntity {

    @Id
    private String id;

    @Column(name = "participant_a", nullable = false)
    private String participantA;

    @Column(name = "participant_b", nullable = false)
    private String participantB;

    @Column(name = "last_message_preview")
    private String lastMessagePreview;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getParticipantA() { return participantA; }
    public void setParticipantA(String participantA) { this.participantA = participantA; }
    public String getParticipantB() { return participantB; }
    public void setParticipantB(String participantB) { this.participantB = participantB; }
    public String getLastMessagePreview() { return lastMessagePreview; }
    public void setLastMessagePreview(String lastMessagePreview) { this.lastMessagePreview = lastMessagePreview; }
    public Instant getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(Instant lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
