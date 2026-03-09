package org.elas.momentum.matching.domain.port.out;

import java.util.List;
import java.util.Optional;

public interface UserProfilePort {

    Optional<CandidateProfile> findById(String userId);

    /**
     * Retourne tous les utilisateurs actifs avec le sport demandé (sauf le requester).
     */
    List<CandidateProfile> findCandidates(String sport, String requesterId);

    record CandidateProfile(
            String userId,
            String sport,
            String proficiency,
            int yearsExperience,
            double latitude,
            double longitude
    ) {}
}
