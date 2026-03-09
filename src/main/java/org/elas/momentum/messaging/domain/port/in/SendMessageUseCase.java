package org.elas.momentum.messaging.domain.port.in;

import org.elas.momentum.messaging.application.dto.MessageResult;
import org.elas.momentum.messaging.application.dto.SendMessageCommand;

public interface SendMessageUseCase {
    MessageResult send(String senderId, SendMessageCommand command);
}
