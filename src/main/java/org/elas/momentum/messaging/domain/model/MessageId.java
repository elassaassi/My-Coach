package org.elas.momentum.messaging.domain.model;

import java.util.UUID;

public record MessageId(String value) {
    public MessageId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("MessageId cannot be blank");
    }
    public static MessageId generate() { return new MessageId(UUID.randomUUID().toString()); }
    public static MessageId of(String value) { return new MessageId(value); }
}
