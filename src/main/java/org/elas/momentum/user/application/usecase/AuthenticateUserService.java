package org.elas.momentum.user.application.usecase;

import org.elas.momentum.user.domain.exception.AccountSuspendedException;
import org.elas.momentum.user.domain.exception.InvalidCredentialsException;
import org.elas.momentum.user.domain.exception.UserNotFoundException;
import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.UserStatus;
import org.elas.momentum.user.AuthenticateUserUseCase;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticateUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResult authenticate(String email, String rawPassword) {
        var user = userRepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new AccountSuspendedException();
        }

        return new AuthResult(user.getId().value(), user.getEmail().value(), "USER");
    }
}
