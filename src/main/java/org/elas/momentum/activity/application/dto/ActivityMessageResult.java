package org.elas.momentum.activity.application.dto;

import java.time.Instant;

public record ActivityMessageResult(
        String  id,
        String  activityId,
        String  senderId,
        String  senderFirstName,
        String  senderLastName,
        String  content,
        Instant sentAt
) {}
