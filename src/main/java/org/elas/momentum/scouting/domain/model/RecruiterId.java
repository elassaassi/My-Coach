package org.elas.momentum.scouting.domain.model;

import java.util.UUID;

public record RecruiterId(String value) {
    public static RecruiterId generate() { return new RecruiterId(UUID.randomUUID().toString()); }
    public static RecruiterId of(String value) { return new RecruiterId(value); }
}
