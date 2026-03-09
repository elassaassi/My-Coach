package org.elas.momentum.user.domain.exception;

import org.elas.momentum.shared.exception.BusinessException;

public class AccountSuspendedException extends BusinessException {
    public AccountSuspendedException() {
        super("ACCOUNT_SUSPENDED", "Compte suspendu");
    }
}
