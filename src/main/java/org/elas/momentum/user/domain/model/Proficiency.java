package org.elas.momentum.user.domain.model;

public enum Proficiency {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    ELITE;

    public boolean isCompatibleWith(Proficiency other) {
        return Math.abs(this.ordinal() - other.ordinal()) <= 1;
    }
}
