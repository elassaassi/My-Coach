package org.elas.momentum.user.domain.port.in;

import org.elas.momentum.user.application.dto.UserResult;

public interface UpdateAvatarUseCase {
    UserResult updateAvatar(String userId, String avatarUrl);
}