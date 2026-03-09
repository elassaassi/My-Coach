package org.elas.momentum.rating.domain.model;

import java.util.UUID;

public record RatingId(String value) {
    public static RatingId generate() { return new RatingId(UUID.randomUUID().toString()); }
    public static RatingId of(String value) { return new RatingId(value); }
}
