package org.elas.momentum.rating.domain.exception;

import org.elas.momentum.shared.exception.BusinessException;

public class AlreadyRatedException extends BusinessException {
    public AlreadyRatedException(String raterId, String ratedUserId) {
        super("ALREADY_RATED", "User " + raterId + " already rated " + ratedUserId + " for this activity");
    }
}
