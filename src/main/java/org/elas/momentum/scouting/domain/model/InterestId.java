package org.elas.momentum.scouting.domain.model;

import java.util.UUID;

public record InterestId(String value) {
    public static InterestId generate() { return new InterestId(UUID.randomUUID().toString()); }
    public static InterestId of(String value) { return new InterestId(value); }
}
