package org.elas.momentum.activity.domain.model;

import java.util.UUID;

public record ActivityId(String value) {
    public ActivityId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("ActivityId cannot be blank");
    }

    public static ActivityId generate() { return new ActivityId(UUID.randomUUID().toString()); }
    public static ActivityId of(String value) { return new ActivityId(value); }
}
