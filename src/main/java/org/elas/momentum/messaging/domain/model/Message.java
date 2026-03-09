package org.elas.momentum.messaging.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Message {

    private final MessageId id;
    private final ConversationId conversationId;
    private final String senderId;
    private final String content;
    private MessageStatus status;
    private final Instant sentAt;

    public static Message create(ConversationId conversationId, String senderId, String content) {
        Objects.requireNonNull(conversationId);
        Objects.requireNonNull(senderId);
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Message content cannot be blank");
        if (content.length() > 2000) throw new IllegalArgumentException("Message too long (max 2000 chars)");
        return new Message(MessageId.generate(), conversationId, senderId, content, MessageStatus.SENT, Instant.now());
    }

    public Message(MessageId id, ConversationId conversationId, String senderId,
                   String content, MessageStatus status, Instant sentAt) {
        this.id = Objects.requireNonNull(id);
        this.conversationId = Objects.requireNonNull(conversationId);
        this.senderId = Objects.requireNonNull(senderId);
        this.content = Objects.requireNonNull(content);
        this.status = status;
        this.sentAt = sentAt;
    }

    public void markRead() { this.status = MessageStatus.READ; }
    public void markDelivered() { if (status == MessageStatus.SENT) this.status = MessageStatus.DELIVERED; }

    public MessageId getId() { return id; }
    public ConversationId getConversationId() { return conversationId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public MessageStatus getStatus() { return status; }
    public Instant getSentAt() { return sentAt; }
}
