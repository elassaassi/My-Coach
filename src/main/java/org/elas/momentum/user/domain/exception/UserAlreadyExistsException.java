package org.elas.momentum.user.domain.exception;

import org.elas.momentum.shared.exception.BusinessException;

public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String email) {
        super("USER_ALREADY_EXISTS", "User already exists with email: " + email);
    }
}
