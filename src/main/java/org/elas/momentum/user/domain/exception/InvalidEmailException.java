package org.elas.momentum.user.domain.exception;

import org.elas.momentum.shared.exception.BusinessException;

public class InvalidEmailException extends BusinessException {
    public InvalidEmailException(String email) {
        super("INVALID_EMAIL", "Invalid email format: " + email);
    }
}
