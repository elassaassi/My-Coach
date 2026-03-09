package org.elas.momentum.messaging.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate Root — Messaging Bounded Context.
 */
public class Conversation {

    private final ConversationId id;
    private final String participantA;
    private final String participantB;
    private String lastMessagePreview;
    private Instant lastMessageAt;
    private final Instant createdAt;

    public static Conversation create(String userA, String userB) {
        Objects.requireNonNull(userA);
        Objects.requireNonNull(userB);
        if (userA.equals(userB)) throw new IllegalArgumentException("Cannot create conversation with yourself");
        return new Conversation(ConversationId.generate(), userA, userB, null, null, Instant.now());
    }

    public Conversation(ConversationId id, String participantA, String participantB,
                        String lastMessagePreview, Instant lastMessageAt, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.participantA = Objects.requireNonNull(participantA);
        this.participantB = Objects.requireNonNull(participantB);
        this.lastMessagePreview = lastMessagePreview;
        this.lastMessageAt = lastMessageAt;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public Message sendMessage(String senderId, String content) {
        if (!isParticipant(senderId)) {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
        var message = Message.create(id, senderId, content);
        this.lastMessagePreview = content.length() > 50 ? content.substring(0, 50) + "…" : content;
        this.lastMessageAt = message.getSentAt();
        return message;
    }

    public boolean isParticipant(String userId) {
        return participantA.equals(userId) || participantB.equals(userId);
    }

    public String getOtherParticipant(String userId) {
        return participantA.equals(userId) ? participantB : participantA;
    }

    public ConversationId getId() { return id; }
    public String getParticipantA() { return participantA; }
    public String getParticipantB() { return participantB; }
    public String getLastMessagePreview() { return lastMessagePreview; }
    public Instant getLastMessageAt() { return lastMessageAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Set<String> getParticipants() { return Set.of(participantA, participantB); }
}
