package org.elas.momentum.activity.application.dto;

import java.time.Instant;

public record ActivityMessageResult(
        String  id,
        String  activityId,
        String  senderId,
        String  content,
        Instant sentAt
) {}
