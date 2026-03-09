package org.elas.momentum.messaging.domain.port.out;

import org.elas.momentum.messaging.domain.model.Message;
import org.elas.momentum.messaging.domain.model.ConversationId;

import java.util.List;

public interface MessageRepository {
    Message save(Message message);
    List<Message> findByConversationId(ConversationId conversationId, int page, int size);
}
