package org.elas.momentum.user.domain.port.in;

import org.elas.momentum.user.application.dto.UserResult;

public interface GetUserUseCase {
    UserResult getById(String userId);
    UserResult getByEmail(String email);
}
