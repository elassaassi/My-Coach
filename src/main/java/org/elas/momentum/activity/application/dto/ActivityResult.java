package org.elas.momentum.activity.application.dto;

import java.time.Instant;
import java.util.List;

public record ActivityResult(
        String id,
        String organizerId,
        String title,
        String description,
        String sport,
        String requiredLevel,
        double latitude,
        double longitude,
        String address,
        String city,
        String country,
        Instant scheduledAt,
        int maxParticipants,
        int currentParticipants,
        String status,
        List<ParticipantDto> participants,
        Instant createdAt
) {
    public record ParticipantDto(String userId, Instant joinedAt) {}
}
