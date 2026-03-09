package org.elas.momentum.messaging.application.usecase;

import org.elas.momentum.messaging.application.dto.ConversationResult;
import org.elas.momentum.messaging.application.dto.MessageResult;
import org.elas.momentum.messaging.domain.model.Conversation;
import org.elas.momentum.messaging.domain.model.ConversationId;
import org.elas.momentum.messaging.domain.port.in.GetConversationUseCase;
import org.elas.momentum.messaging.domain.port.out.ConversationRepository;
import org.elas.momentum.messaging.domain.port.out.MessageRepository;
import org.elas.momentum.messaging.infrastructure.persistence.MessagingMapper;
import org.elas.momentum.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetConversationService implements GetConversationUseCase {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public GetConversationService(ConversationRepository conversationRepository,
                                  MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ConversationResult> getMyConversations(String userId) {
        return conversationRepository.findByParticipant(userId).stream()
                .map(c -> MessagingMapper.toConversationResult(c, userId))
                .toList();
    }

    @Override
    public List<MessageResult> getMessages(String conversationId, String userId, int page, int size) {
        var conversation = conversationRepository.findById(ConversationId.of(conversationId))
                .orElseThrow(() -> new NotFoundException("Conversation", conversationId));

        if (!conversation.isParticipant(userId)) {
            throw new IllegalStateException("Access denied to this conversation");
        }

        return messageRepository.findByConversationId(ConversationId.of(conversationId), page, size)
                .stream()
                .map(MessagingMapper::toMessageResult)
                .toList();
    }

    @Override
    @Transactional
    public ConversationResult getOrCreateConversation(String userId, String otherUserId) {
        Conversation conversation = conversationRepository
                .findByParticipants(userId, otherUserId)
                .orElseGet(() -> {
                    Conversation newConv = Conversation.create(userId, otherUserId);
                    return conversationRepository.save(newConv);
                });

        return MessagingMapper.toConversationResult(conversation, userId);
    }
}
