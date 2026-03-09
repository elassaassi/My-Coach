package org.elas.momentum.activity.infrastructure.persistence;

import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.domain.model.*;

import java.util.ArrayList;
import java.util.List;

public final class ActivityMapper {

    private ActivityMapper() {}

    public static Activity toDomain(ActivityEntity e) {
        var location = new Location(
                e.getLatitude(),
                e.getLongitude(),
                e.getAddress() != null ? e.getAddress() : "",
                e.getCity(),
                e.getCountry() != null ? e.getCountry() : "");

        List<Participant> participants = e.getParticipants().stream()
                .map(pe -> new Participant(pe.getUserId(), pe.getJoinedAt()))
                .toList();

        return Activity.reconstitute(
                ActivityId.of(e.getId()),
                e.getOrganizerId(),
                e.getTitle(),
                e.getDescription(),
                e.getSport(),
                e.getRequiredLevel(),
                location,
                e.getScheduledAt(),
                e.getMaxParticipants(),
                ActivityStatus.valueOf(e.getStatus()),
                participants,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public static ActivityEntity toEntity(Activity a) {
        var e = new ActivityEntity();
        e.setId(a.getId().value());
        e.setOrganizerId(a.getOrganizerId());
        e.setTitle(a.getTitle());
        e.setDescription(a.getDescription());
        e.setSport(a.getSport());
        e.setRequiredLevel(a.getRequiredLevel());
        e.setLatitude(a.getLocation().latitude());
        e.setLongitude(a.getLocation().longitude());
        e.setAddress(a.getLocation().venueName());   // venueName → address column (no migration needed)
        e.setCity(a.getLocation().city());
        e.setCountry(a.getLocation().country());
        e.setScheduledAt(a.getScheduledAt());
        e.setMaxParticipants(a.getMaxParticipants());
        e.setStatus(a.getStatus().name());
        e.setCreatedAt(a.getCreatedAt());
        e.setUpdatedAt(a.getUpdatedAt());

        List<ParticipantEntity> participantEntities = a.getParticipants().stream()
                .map(p -> {
                    var pe = new ParticipantEntity();
                    pe.setUserId(p.userId());
                    pe.setJoinedAt(p.joinedAt());
                    pe.setActivity(e);
                    return pe;
                })
                .toList();
        e.setParticipants(new ArrayList<>(participantEntities));
        return e;
    }

    public static ActivityResult toResult(Activity a) {
        var participants = a.getParticipants().stream()
                .map(p -> new ActivityResult.ParticipantDto(p.userId(), p.joinedAt()))
                .toList();

        var location = new ActivityResult.LocationDto(
                a.getLocation().latitude(),
                a.getLocation().longitude(),
                a.getLocation().venueName(),
                a.getLocation().city(),
                a.getLocation().country()
        );

        return new ActivityResult(
                a.getId().value(),
                a.getOrganizerId(),
                a.getTitle(),
                a.getDescription(),
                a.getSport(),
                a.getRequiredLevel(),
                location,
                a.getScheduledAt(),
                a.getMaxParticipants(),
                a.getCurrentParticipantsCount(),
                a.getStatus().name(),
                participants,
                a.getCreatedAt()
        );
    }
}
