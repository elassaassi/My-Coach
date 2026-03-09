package org.elas.momentum.messaging.application.dto;

import java.time.Instant;

public record ConversationResult(
        String id,
        String participantA,
        String participantB,
        String otherParticipantId,
        String lastMessagePreview,
        Instant lastMessageAt,
        Instant createdAt
) {}
