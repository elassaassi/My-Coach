package org.elas.momentum.matching.infrastructure.client;

import org.elas.momentum.matching.domain.port.out.UserProfilePort;
import org.elas.momentum.user.UserModuleAPI;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter sortant — accès cross-module via l'API publique du module User.
 * Respecte les règles Spring Modulith : on ne dépend que de l'interface publique.
 */
@Component
class UserProfileAdapter implements UserProfilePort {

    private final UserModuleAPI userModuleAPI;

    UserProfileAdapter(UserModuleAPI userModuleAPI) {
        this.userModuleAPI = userModuleAPI;
    }

    @Override
    public Optional<CandidateProfile> findById(String userId) {
        return userModuleAPI.findById(userId)
                .flatMap(u -> u.sports().stream()
                        .findFirst()
                        .map(sl -> new CandidateProfile(u.id(), sl.sport(), sl.proficiency(),
                                sl.yearsExperience(), u.latitude(), u.longitude())));
    }

    @Override
    public List<CandidateProfile> findCandidates(String sport, String requesterId) {
        return userModuleAPI.findBySport(sport, requesterId).stream()
                .flatMap(u -> u.sports().stream()
                        .filter(sl -> sl.sport().equals(sport))
                        .map(sl -> new CandidateProfile(
                                u.id(), sl.sport(), sl.proficiency(),
                                sl.yearsExperience(), u.latitude(), u.longitude())))
                .toList();
    }
}
