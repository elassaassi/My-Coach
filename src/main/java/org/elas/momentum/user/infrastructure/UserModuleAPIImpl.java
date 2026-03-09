package org.elas.momentum.user.infrastructure;

import org.elas.momentum.user.UserModuleAPI;
import org.elas.momentum.user.UserSummary;
import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.User;
import org.elas.momentum.user.domain.model.UserId;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserModuleAPIImpl implements UserModuleAPI {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserModuleAPIImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserSummary> findById(String userId) {
        return userRepository.findById(UserId.of(userId))
                .map(user -> {
                    var sports = user.getSportProfile().sports().stream()
                            .map(sl -> new UserSummary.SportLevelSummary(
                                    sl.sport(), sl.proficiency().name(), sl.yearsExperience()))
                            .toList();

                    return new UserSummary(
                            user.getId().value(),
                            user.getEmail().value(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getAvatarUrl(),
                            user.getStatus().name(),
                            sports,
                            user.getSportProfile().latitude(),
                            user.getSportProfile().longitude(),
                            user.getSportProfile().city(),
                            user.getSportProfile().country()
                    );
                });
    }

    @Override
    public boolean exists(String userId) {
        return userRepository.findById(UserId.of(userId)).isPresent();
    }

    @Override
    @Transactional
    public String findOrCreateOAuthUser(String email, String firstName, String lastName) {
        return userRepository.findByEmail(Email.of(email))
                .map(u -> u.getId().value())
                .orElseGet(() -> {
                    // Crée un compte avec un mot de passe aléatoire (l'utilisateur se connectera via OAuth2)
                    String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
                    User user = User.register(Email.of(email), randomPassword,
                            firstName.isBlank() ? "Utilisateur" : firstName,
                            lastName.isBlank() ? "" : lastName);
                    return userRepository.save(user).getId().value();
                });
    }
}
