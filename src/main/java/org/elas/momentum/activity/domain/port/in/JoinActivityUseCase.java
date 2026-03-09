package org.elas.momentum.activity.domain.port.in;

import org.elas.momentum.activity.application.dto.ActivityResult;

public interface JoinActivityUseCase {
    ActivityResult join(String activityId, String userId);
    ActivityResult leave(String activityId, String userId);
}
