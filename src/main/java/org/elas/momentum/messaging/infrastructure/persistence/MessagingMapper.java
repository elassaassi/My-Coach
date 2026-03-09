package org.elas.momentum.messaging.infrastructure.persistence;

import org.elas.momentum.messaging.application.dto.ConversationResult;
import org.elas.momentum.messaging.application.dto.MessageResult;
import org.elas.momentum.messaging.domain.model.*;

public final class MessagingMapper {

    private MessagingMapper() {}

    public static Conversation toConversationDomain(ConversationEntity e) {
        return new Conversation(
                ConversationId.of(e.getId()),
                e.getParticipantA(),
                e.getParticipantB(),
                e.getLastMessagePreview(),
                e.getLastMessageAt(),
                e.getCreatedAt()
        );
    }

    public static ConversationEntity toConversationEntity(Conversation c) {
        var e = new ConversationEntity();
        e.setId(c.getId().value());
        e.setParticipantA(c.getParticipantA());
        e.setParticipantB(c.getParticipantB());
        e.setLastMessagePreview(c.getLastMessagePreview());
        e.setLastMessageAt(c.getLastMessageAt());
        e.setCreatedAt(c.getCreatedAt());
        return e;
    }

    public static Message toMessageDomain(MessageEntity e) {
        return new Message(
                MessageId.of(e.getId()),
                ConversationId.of(e.getConversationId()),
                e.getSenderId(),
                e.getContent(),
                MessageStatus.valueOf(e.getStatus()),
                e.getSentAt()
        );
    }

    public static MessageEntity toMessageEntity(Message m) {
        var e = new MessageEntity();
        e.setId(m.getId().value());
        e.setConversationId(m.getConversationId().value());
        e.setSenderId(m.getSenderId());
        e.setContent(m.getContent());
        e.setStatus(m.getStatus().name());
        e.setSentAt(m.getSentAt());
        return e;
    }

    public static MessageResult toMessageResult(Message m) {
        return new MessageResult(
                m.getId().value(),
                m.getConversationId().value(),
                m.getSenderId(),
                m.getContent(),
                m.getStatus().name(),
                m.getSentAt()
        );
    }

    public static ConversationResult toConversationResult(Conversation c, String currentUserId) {
        return new ConversationResult(
                c.getId().value(),
                c.getParticipantA(),
                c.getParticipantB(),
                c.getOtherParticipant(currentUserId),
                c.getLastMessagePreview(),
                c.getLastMessageAt(),
                c.getCreatedAt()
        );
    }
}
