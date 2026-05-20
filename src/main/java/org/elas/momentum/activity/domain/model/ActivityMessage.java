package org.elas.momentum.activity.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ActivityMessage(
        String  id,
        String  activityId,
        String  senderId,
        String  content,
        Instant sentAt
) {
    public static ActivityMessage create(String activityId, String senderId, String content) {
        return new ActivityMessage(
                UUID.randomUUID().toString(),
                activityId,
                senderId,
                content.trim(),
                Instant.now()
        );
    }
}