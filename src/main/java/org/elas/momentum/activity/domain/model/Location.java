package org.elas.momentum.activity.domain.model;

import java.util.Objects;

public record Location(
        double latitude,
        double longitude,
        String venueName,
        String city,
        String country
) {
    public Location {
        Objects.requireNonNull(city);
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Invalid latitude");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Invalid longitude");
    }
}
