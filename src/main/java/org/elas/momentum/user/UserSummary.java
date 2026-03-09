package org.elas.momentum.user;

import java.util.List;

/**
 * API publique du module User — exposé aux autres modules.
 */
public record UserSummary(
        String id,
        String email,
        String firstName,
        String lastName,
        String avatarUrl,
        String status,
        List<SportLevelSummary> sports,
        double latitude,
        double longitude,
        String city,
        String country
) {
    public record SportLevelSummary(String sport, String proficiency, int yearsExperience) {}
}
