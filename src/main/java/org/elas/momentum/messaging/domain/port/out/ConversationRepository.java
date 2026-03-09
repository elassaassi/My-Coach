package org.elas.momentum.messaging.domain.port.out;

import org.elas.momentum.messaging.domain.model.Conversation;
import org.elas.momentum.messaging.domain.model.ConversationId;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository {
    Conversation save(Conversation conversation);
    Optional<Conversation> findById(ConversationId id);
    Optional<Conversation> findByParticipants(String userA, String userB);
    List<Conversation> findByParticipant(String userId);
}
