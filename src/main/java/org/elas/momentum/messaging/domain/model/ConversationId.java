package org.elas.momentum.messaging.domain.model;

import java.util.UUID;

public record ConversationId(String value) {
    public ConversationId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("ConversationId cannot be blank");
    }
    public static ConversationId generate() { return new ConversationId(UUID.randomUUID().toString()); }
    public static ConversationId of(String value) { return new ConversationId(value); }
}
