package org.elas.momentum.activity.domain.port.in;

import org.elas.momentum.activity.application.dto.ActivityMessageResult;
import org.elas.momentum.activity.application.dto.SendMessageCommand;

public interface SendMessageUseCase {
    ActivityMessageResult send(String activityId, String senderId, SendMessageCommand command);
}