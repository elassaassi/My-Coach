package org.elas.momentum.activity.domain.model;

import java.time.Instant;

public record Participant(String userId, Instant joinedAt) {
    public static Participant of(String userId) {
        return new Participant(userId, Instant.now());
    }
}
