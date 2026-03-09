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
        LocationDto location,
        Instant scheduledAt,
        int maxParticipants,
        int currentParticipantsCount,
        String status,
        List<ParticipantDto> participants,
        Instant createdAt
) {
    public record LocationDto(
            double latitude,
            double longitude,
            String venueName,
            String city,
            String country
    ) {}

    public record ParticipantDto(String userId, Instant joinedAt) {}
}
