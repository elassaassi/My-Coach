package org.elas.momentum.user.domain.model;

import java.util.Objects;

public record SportLevel(String sport, Proficiency proficiency, int yearsExperience) {

    public SportLevel {
        Objects.requireNonNull(sport, "Sport cannot be null");
        Objects.requireNonNull(proficiency, "Proficiency cannot be null");
        if (sport.isBlank()) throw new IllegalArgumentException("Sport cannot be blank");
        if (yearsExperience < 0) throw new IllegalArgumentException("Years of experience cannot be negative");
    }
}
