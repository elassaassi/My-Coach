package org.elas.momentum.messaging.infrastructure.persistence;

import org.elas.momentum.messaging.domain.model.Conversation;
import org.elas.momentum.messaging.domain.model.ConversationId;
import org.elas.momentum.messaging.domain.model.Message;
import org.elas.momentum.messaging.domain.port.out.ConversationRepository;
import org.elas.momentum.messaging.domain.port.out.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class MessagingPersistenceAdapter implements ConversationRepository, MessageRepository {

    private final ConversationJpaRepository convRepo;
    private final MessageJpaRepository msgRepo;

    MessagingPersistenceAdapter(ConversationJpaRepository convRepo,
                                MessageJpaRepository msgRepo) {
        this.convRepo = convRepo;
        this.msgRepo = msgRepo;
    }

    // ── ConversationRepository ────────────────────────────────────────────────

    @Override
    public Conversation save(Conversation conversation) {
        return MessagingMapper.toConversationDomain(
                convRepo.save(MessagingMapper.toConversationEntity(conversation)));
    }

    @Override
    public Optional<Conversation> findById(ConversationId id) {
        return convRepo.findById(id.value()).map(MessagingMapper::toConversationDomain);
    }

    @Override
    public Optional<Conversation> findByParticipants(String userA, String userB) {
        return convRepo.findByParticipants(userA, userB).map(MessagingMapper::toConversationDomain);
    }

    @Override
    public List<Conversation> findByParticipant(String userId) {
        return convRepo.findByParticipant(userId).stream()
                .map(MessagingMapper::toConversationDomain)
                .toList();
    }

    // ── MessageRepository ─────────────────────────────────────────────────────

    @Override
    public Message save(Message message) {
        return MessagingMapper.toMessageDomain(
                msgRepo.save(MessagingMapper.toMessageEntity(message)));
    }

    @Override
    public List<Message> findByConversationId(ConversationId conversationId, int page, int size) {
        return msgRepo.findByConversationIdOrderBySentAtDesc(
                        conversationId.value(), PageRequest.of(page, size))
                .stream()
                .map(MessagingMapper::toMessageDomain)
                .toList();
    }
}
