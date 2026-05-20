package org.elas.momentum.user.infrastructure.web.dto;

import org.elas.momentum.user.application.dto.UserResult;

import java.util.List;

/**
 * Profile visible by other authenticated users.
 * Excludes email (private) and precise GPS coordinates (privacy).
 */
public record PublicUserResult(
        String id,
        String firstName,
        String lastName,
        String avatarUrl,
        String status,
        List<UserResult.SportLevelDto> sports,
        String city,
        String country
) {
    public static PublicUserResult from(UserResult u) {
        var profile = u.sportProfile();
        return new PublicUserResult(
                u.id(),
                u.firstName(),
                u.lastName(),
                u.avatarUrl(),
                u.status(),
                profile != null ? profile.sports() : List.of(),
                profile != null ? profile.city()   : null,
                profile != null ? profile.country() : null
        );
    }
}