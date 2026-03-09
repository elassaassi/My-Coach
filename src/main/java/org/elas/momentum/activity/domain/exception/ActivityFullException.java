package org.elas.momentum.activity.domain.exception;

import org.elas.momentum.shared.exception.BusinessException;

public class ActivityFullException extends BusinessException {
    public ActivityFullException(String activityId) {
        super("ACTIVITY_FULL", "Activity is full: " + activityId);
    }
}
