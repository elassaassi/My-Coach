package org.elas.momentum.coaching.domain.model;

import java.util.UUID;

public record OfferId(String value) {
    public static OfferId generate() { return new OfferId(UUID.randomUUID().toString()); }
    public static OfferId of(String value) { return new OfferId(value); }
}
