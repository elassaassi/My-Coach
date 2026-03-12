package org.elas.momentum.activity.domain.port.in;

import org.elas.momentum.activity.application.dto.ActivityResult;

public interface CompleteActivityUseCase {
    ActivityResult complete(String activityId, String requesterId);
}