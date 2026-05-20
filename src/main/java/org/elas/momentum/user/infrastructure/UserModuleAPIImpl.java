package org.elas.momentum.user.infrastructure;

import org.elas.momentum.user.UserModuleAPI;
import org.elas.momentum.user.UserSummary;
import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.User;
import org.elas.momentum.user.domain.model.UserId;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserModuleAPIImpl implements UserModuleAPI {

    private final UserRepository userRepository;

    public UserModuleAPIImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserSummary> findById(String userId) {
        return userRepository.findById(UserId.of(userId)).map(this::toSummary);
    }

    @Override
    public Map<String, UserSummary> findByIds(Set<String> userIds) {
        var ids = userIds.stream().map(UserId::of).toList();
        return userRepository.findByIds(ids).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toSummary(e.getValue())));
    }

    @Override
    public boolean exists(String userId) {
        return userRepository.findById(UserId.of(userId)).isPresent();
    }

    @Override
    public List<UserSummary> findBySport(String sport, String excludeUserId) {
        return userRepository.findBySport(sport, excludeUserId).stream()
                .map(this::toSummary)
                .toList();
    }

    private UserSummary toSummary(User user) {
        var sports = user.getSportProfile().sports().stream()
                .map(sl -> new UserSummary.SportLevelSummary(sl.sport(), sl.proficiency().name(), sl.yearsExperience()))
                .toList();
        return new UserSummary(
                user.getId().value(), user.getEmail().value(),
                user.getFirstName(), user.getLastName(),
                user.getAvatarUrl(), user.getStatus().name(),
                sports,
                user.getSportProfile().latitude(), user.getSportProfile().longitude(),
                user.getSportProfile().city(), user.getSportProfile().country());
    }

    @Override
    @Transactional
    public String findOrCreateOAuthUser(String email, String firstName, String lastName) {
        return userRepository.findByEmail(Email.of(email))
                .map(u -> u.getId().value())
                .orElseGet(() -> {
                    User user = User.registerViaOAuth(
                            Email.of(email),
                            firstName.isBlank() ? "Utilisateur" : firstName,
                            lastName.isBlank() ? "" : lastName
                    );
                    return userRepository.save(user).getId().value();
                });
    }
}
