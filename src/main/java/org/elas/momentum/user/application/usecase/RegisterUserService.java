package org.elas.momentum.user.application.usecase;

import org.elas.momentum.user.application.dto.RegisterUserCommand;
import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.exception.UserAlreadyExistsException;
import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.User;
import org.elas.momentum.user.domain.port.in.RegisterUserUseCase;
import org.elas.momentum.user.domain.port.out.UserEventPublisher;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.elas.momentum.user.infrastructure.persistence.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepository userRepository,
                               UserEventPublisher eventPublisher,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResult register(RegisterUserCommand command) {
        Email email = Email.of(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(command.email());
        }

        String passwordHash = passwordEncoder.encode(command.password());
        User user = User.register(email, passwordHash, command.firstName(), command.lastName());
        user.activate(); // Phase 0 : pas de vérification email

        User saved = userRepository.save(user);
        eventPublisher.publish(saved.pullDomainEvents());

        return UserMapper.toResult(saved);
    }
}
