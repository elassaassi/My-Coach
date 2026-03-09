package org.elas.momentum.user.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SportProfile(
        List<SportLevel> sports,
        double latitude,
        double longitude,
        String city,
        String country
) {
    public SportProfile {
        Objects.requireNonNull(sports, "Sports cannot be null");
        sports = List.copyOf(sports);
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Invalid latitude: " + latitude);
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Invalid longitude: " + longitude);
    }

    public Optional<SportLevel> getSportLevel(String sport) {
        return sports.stream()
                .filter(s -> s.sport().equalsIgnoreCase(sport))
                .findFirst();
    }

    public static SportProfile empty() {
        return new SportProfile(List.of(), 0.0, 0.0, "", "");
    }
}
