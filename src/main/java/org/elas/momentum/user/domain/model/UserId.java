package org.elas.momentum.user.domain.model;

import java.util.Objects;
import java.util.UUID;

public record UserId(String value) {

    public UserId {
        Objects.requireNonNull(value, "UserId cannot be null");
        if (value.isBlank()) throw new IllegalArgumentException("UserId cannot be blank");
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
