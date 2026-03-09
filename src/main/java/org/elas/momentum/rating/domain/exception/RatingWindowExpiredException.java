package org.elas.momentum.rating.domain.exception;

import org.elas.momentum.shared.exception.BusinessException;

public class RatingWindowExpiredException extends BusinessException {
    public RatingWindowExpiredException(String activityId) {
        super("RATING_WINDOW_EXPIRED", "Rating window expired for activity: " + activityId);
    }
}
