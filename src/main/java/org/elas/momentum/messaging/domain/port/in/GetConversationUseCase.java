package org.elas.momentum.messaging.domain.port.in;

import org.elas.momentum.messaging.application.dto.ConversationResult;
import org.elas.momentum.messaging.application.dto.MessageResult;

import java.util.List;

public interface GetConversationUseCase {
    List<ConversationResult> getMyConversations(String userId);
    List<MessageResult> getMessages(String conversationId, String userId, int page, int size);
    ConversationResult getOrCreateConversation(String userId, String otherUserId);
}
