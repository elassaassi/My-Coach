package org.elas.momentum.user.domain.port.out;

import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.User;
import org.elas.momentum.user.domain.model.UserId;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
}
