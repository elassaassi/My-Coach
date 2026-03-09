package org.elas.momentum.user.application.dto;

import java.util.List;

public record UpdateProfileCommand(
        String firstName,
        String lastName,
        List<SportLevelDto> sports,
        double latitude,
        double longitude,
        String city,
        String country
) {
    public record SportLevelDto(String sport, String proficiency, int yearsExperience) {}
}
