package org.elas.momentum.user.domain.event;

import org.elas.momentum.shared.domain.DomainEvent;
import org.elas.momentum.user.domain.model.UserId;

import java.time.Instant;
import java.util.UUID;

public record ProfileUpdatedEvent(
        UUID eventId,
        Instant occurredAt,
        UserId userId
) implements DomainEvent {

    public static ProfileUpdatedEvent of(UserId userId) {
        return new ProfileUpdatedEvent(UUID.randomUUID(), Instant.now(), userId);
    }
}
