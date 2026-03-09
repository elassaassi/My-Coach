package org.elas.momentum.user.domain.port.in;

import org.elas.momentum.user.application.dto.RegisterUserCommand;
import org.elas.momentum.user.application.dto.UserResult;

public interface RegisterUserUseCase {
    UserResult register(RegisterUserCommand command);
}
