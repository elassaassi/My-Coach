package org.elas.momentum.user.application.usecase;

import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.exception.UserNotFoundException;
import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.UserId;
import org.elas.momentum.user.domain.port.in.GetUserUseCase;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.elas.momentum.user.infrastructure.persistence.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetUserService implements GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResult getById(String userId) {
        return userRepository.findById(UserId.of(userId))
                .map(UserMapper::toResult)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public UserResult getByEmail(String email) {
        return userRepository.findByEmail(Email.of(email))
                .map(UserMapper::toResult)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
