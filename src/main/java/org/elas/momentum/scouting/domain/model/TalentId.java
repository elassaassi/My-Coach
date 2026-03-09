package org.elas.momentum.scouting.domain.model;

import java.util.UUID;

public record TalentId(String value) {
    public static TalentId generate() { return new TalentId(UUID.randomUUID().toString()); }
    public static TalentId of(String value) { return new TalentId(value); }
}
