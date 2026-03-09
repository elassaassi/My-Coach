package org.elas.momentum.coaching.domain.model;

import java.util.UUID;

public record CoachId(String value) {
    public static CoachId generate() { return new CoachId(UUID.randomUUID().toString()); }
    public static CoachId of(String value) { return new CoachId(value); }
}
