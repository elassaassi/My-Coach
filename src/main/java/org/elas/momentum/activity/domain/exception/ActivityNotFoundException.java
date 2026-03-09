package org.elas.momentum.activity.domain.exception;

import org.elas.momentum.shared.exception.NotFoundException;

public class ActivityNotFoundException extends NotFoundException {
    public ActivityNotFoundException(String id) {
        super("Activity", id);
    }
}
