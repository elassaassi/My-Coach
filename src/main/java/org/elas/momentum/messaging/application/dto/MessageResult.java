package org.elas.momentum.messaging.application.dto;

import java.time.Instant;

public record MessageResult(
        String id,
        String conversationId,
        String senderId,
        String content,
        String status,
        Instant sentAt
) {}
