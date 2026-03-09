package org.elas.momentum.user.application.dto;

import java.time.Instant;
import java.util.List;

public record UserResult(
        String id,
        String email,
        String firstName,
        String lastName,
        String avatarUrl,
        String status,
        SportProfileDto sportProfile,
        Instant createdAt
) {
    public record SportProfileDto(
            List<SportLevelDto> sports,
            double latitude,
            double longitude,
            String city,
            String country
    ) {}

    public record SportLevelDto(String sport, String proficiency, int yearsExperience) {}
}
