package org.elas.momentum.user.application.usecase;

import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.exception.UserNotFoundException;
import org.elas.momentum.user.domain.model.UserId;
import org.elas.momentum.user.domain.port.in.UpdateAvatarUseCase;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.elas.momentum.user.infrastructure.persistence.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateAvatarService implements UpdateAvatarUseCase {

    private final UserRepository userRepository;

    public UpdateAvatarService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResult updateAvatar(String userId, String avatarUrl) {
        var user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.updateAvatar(avatarUrl);
        var saved = userRepository.save(user);
        return UserMapper.toResult(saved);
    }
}