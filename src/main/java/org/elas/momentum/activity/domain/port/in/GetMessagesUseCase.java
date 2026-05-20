package org.elas.momentum.activity.domain.port.in;

import org.elas.momentum.activity.application.dto.ActivityMessageResult;

import java.util.List;

public interface GetMessagesUseCase {
    List<ActivityMessageResult> getMessages(String activityId, int limit);
}