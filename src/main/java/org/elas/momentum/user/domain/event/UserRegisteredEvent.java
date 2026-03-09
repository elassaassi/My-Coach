package org.elas.momentum.user.domain.event;

import org.elas.momentum.shared.domain.DomainEvent;
import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.UserId;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        Instant occurredAt,
        UserId userId,
        Email email,
        String firstName,
        String lastName
) implements DomainEvent {

    public static UserRegisteredEvent of(UserId userId, Email email, String firstName, String lastName) {
        return new UserRegisteredEvent(UUID.randomUUID(), Instant.now(), userId, email, firstName, lastName);
    }
}
