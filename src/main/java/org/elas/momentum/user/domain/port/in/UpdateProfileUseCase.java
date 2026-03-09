package org.elas.momentum.user.domain.port.in;

import org.elas.momentum.user.application.dto.UpdateProfileCommand;
import org.elas.momentum.user.application.dto.UserResult;

public interface UpdateProfileUseCase {
    UserResult updateProfile(String userId, UpdateProfileCommand command);
}
