package org.elas.momentum.rating.domain.event;

import org.elas.momentum.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ManOfMatchEvent(
        UUID eventId,
        String activityId,
        String userId,
        Instant occurredAt
) implements DomainEvent {

    public static ManOfMatchEvent of(String activityId, String userId) {
        return new ManOfMatchEvent(UUID.randomUUID(), activityId, userId, Instant.now());
    }

    @Override
    public Instant occurredAt() { return occurredAt; }
}
