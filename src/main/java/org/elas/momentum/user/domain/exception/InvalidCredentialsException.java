package org.elas.momentum.user.domain.exception;

import org.elas.momentum.shared.exception.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Email ou mot de passe incorrect");
    }
}
