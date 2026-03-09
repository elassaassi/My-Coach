package org.elas.momentum.messaging.application.usecase;

import org.elas.momentum.messaging.application.dto.MessageResult;
import org.elas.momentum.messaging.application.dto.SendMessageCommand;
import org.elas.momentum.messaging.domain.model.Conversation;
import org.elas.momentum.messaging.domain.model.Message;
import org.elas.momentum.messaging.domain.port.in.SendMessageUseCase;
import org.elas.momentum.messaging.domain.port.out.ConversationRepository;
import org.elas.momentum.messaging.domain.port.out.MessageRepository;
import org.elas.momentum.messaging.infrastructure.persistence.MessagingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SendMessageService implements SendMessageUseCase {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public SendMessageService(ConversationRepository conversationRepository,
                              MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public MessageResult send(String senderId, SendMessageCommand command) {
        // Trouver ou créer la conversation
        Conversation conversation = conversationRepository
                .findByParticipants(senderId, command.recipientId())
                .orElseGet(() -> {
                    Conversation newConv = Conversation.create(senderId, command.recipientId());
                    return conversationRepository.save(newConv);
                });

        // Envoyer le message via l'aggregate
        Message message = conversation.sendMessage(senderId, command.content());

        // Persister les deux
        conversationRepository.save(conversation);
        Message saved = messageRepository.save(message);

        return MessagingMapper.toMessageResult(saved);
    }
}
