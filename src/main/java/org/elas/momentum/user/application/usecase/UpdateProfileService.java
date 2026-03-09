package org.elas.momentum.user.application.usecase;

import org.elas.momentum.user.application.dto.UpdateProfileCommand;
import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.exception.UserNotFoundException;
import org.elas.momentum.user.domain.model.Proficiency;
import org.elas.momentum.user.domain.model.SportLevel;
import org.elas.momentum.user.domain.model.SportProfile;
import org.elas.momentum.user.domain.model.UserId;
import org.elas.momentum.user.domain.port.in.UpdateProfileUseCase;
import org.elas.momentum.user.domain.port.out.UserEventPublisher;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.elas.momentum.user.infrastructure.persistence.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;

    public UpdateProfileService(UserRepository userRepository, UserEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserResult updateProfile(String userId, UpdateProfileCommand command) {
        var user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<SportLevel> sportLevels = command.sports() == null ? List.of() :
                command.sports().stream()
                        .map(s -> new SportLevel(s.sport(),
                                Proficiency.valueOf(s.proficiency().toUpperCase()),
                                s.yearsExperience()))
                        .toList();

        var sportProfile = new SportProfile(
                sportLevels,
                command.latitude(),
                command.longitude(),
                command.city(),
                command.country()
        );

        user.updateProfile(sportProfile, command.firstName(), command.lastName());

        var saved = userRepository.save(user);
        eventPublisher.publish(saved.pullDomainEvents());

        return UserMapper.toResult(saved);
    }
}
