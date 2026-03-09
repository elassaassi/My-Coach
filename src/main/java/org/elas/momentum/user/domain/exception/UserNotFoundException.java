package org.elas.momentum.user.domain.exception;

import org.elas.momentum.shared.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String userId) {
        super("User", userId);
    }
}
