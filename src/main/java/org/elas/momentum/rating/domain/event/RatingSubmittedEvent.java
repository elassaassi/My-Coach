package org.elas.momentum.rating.domain.event;

import org.elas.momentum.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record RatingSubmittedEvent(
        UUID eventId,
        String ratingId,
        String activityId,
        String ratedUserId,
        int proScore,
        Instant occurredAt
) implements DomainEvent {

    public static RatingSubmittedEvent of(String ratingId, String activityId,
                                          String ratedUserId, int proScore) {
        return new RatingSubmittedEvent(UUID.randomUUID(), ratingId, activityId, ratedUserId,
                proScore, Instant.now());
    }

    @Override
    public Instant occurredAt() { return occurredAt; }
}
