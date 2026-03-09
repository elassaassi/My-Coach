package org.elas.momentum.matching.domain.model;

import java.util.UUID;

public record MatchRequestId(String value) {

    public MatchRequestId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("MatchRequestId cannot be blank");
    }

    public static MatchRequestId generate() {
        return new MatchRequestId(UUID.randomUUID().toString());
    }

    public static MatchRequestId of(String value) {
        return new MatchRequestId(value);
    }
}
