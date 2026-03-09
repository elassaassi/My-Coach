package org.elas.momentum.user.infrastructure.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        List<SportLevelDto> sports,

        @Min(value = -90, message = "Latitude invalide")
        @Max(value = 90, message = "Latitude invalide")
        double latitude,

        @Min(value = -180, message = "Longitude invalide")
        @Max(value = 180, message = "Longitude invalide")
        double longitude,

        String city,
        String country
) {
    public record SportLevelDto(String sport, String proficiency, int yearsExperience) {}
}
