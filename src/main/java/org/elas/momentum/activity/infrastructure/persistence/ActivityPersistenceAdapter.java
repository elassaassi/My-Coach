package org.elas.momentum.activity.infrastructure.persistence;

import org.elas.momentum.activity.domain.model.Activity;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.model.ActivityStatus;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
class ActivityPersistenceAdapter implements ActivityRepository {

    private final ActivityJpaRepository jpaRepository;

    ActivityPersistenceAdapter(ActivityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Activity save(Activity activity) {
        // Load existing entity with participants in one query to preserve BIGSERIAL ids.
        ActivityEntity existing = jpaRepository.findWithParticipantsById(activity.getId().value()).orElse(null);

        if (existing == null) {
            return ActivityMapper.toDomain(jpaRepository.save(ActivityMapper.toEntity(activity)));
        }

        // Update scalar fields
        existing.setTitle(activity.getTitle());
        existing.setDescription(activity.getDescription());
        existing.setSport(activity.getSport());
        existing.setRequiredLevel(activity.getRequiredLevel());
        existing.setLatitude(activity.getLocation().latitude());
        existing.setLongitude(activity.getLocation().longitude());
        existing.setAddress(activity.getLocation().venueName());
        existing.setCity(activity.getLocation().city());
        existing.setCountry(activity.getLocation().country());
        existing.setScheduledAt(activity.getScheduledAt());
        existing.setMaxParticipants(activity.getMaxParticipants());
        existing.setStatus(activity.getStatus().name());
        existing.setUpdatedAt(activity.getUpdatedAt());

        // Sync participants: keep existing rows (preserve id), add new, remove departed
        Set<String> existingUserIds = existing.getParticipants().stream()
                .map(ParticipantEntity::getUserId)
                .collect(Collectors.toSet());
        Set<String> targetUserIds = activity.getParticipants().stream()
                .map(p -> p.userId())
                .collect(Collectors.toSet());

        // Add new participants
        activity.getParticipants().stream()
                .filter(p -> !existingUserIds.contains(p.userId()))
                .forEach(p -> {
                    var pe = new ParticipantEntity();
                    pe.setUserId(p.userId());
                    pe.setJoinedAt(p.joinedAt());
                    pe.setActivity(existing);
                    existing.getParticipants().add(pe);
                });

        // Remove participants who left
        existing.getParticipants().removeIf(pe -> !targetUserIds.contains(pe.getUserId()));

        return ActivityMapper.toDomain(jpaRepository.save(existing));
    }

    @Override
    public Optional<Activity> findById(ActivityId id) {
        // Use EntityGraph to load participants in a single query (avoids N+1 on detail page)
        return jpaRepository.findWithParticipantsById(id.value()).map(ActivityMapper::toDomain);
    }

    private static final Instant DATE_MIN = Instant.parse("2000-01-01T00:00:00Z");
    private static final Instant DATE_MAX = Instant.parse("2100-12-31T23:59:59Z");

    @Override
    public List<Activity> search(String sport, String city, String status, Instant dateFrom, Instant dateTo, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpaRepository.search(lower(sport), lower(city), blankToNull(status),
                        dateFrom != null ? dateFrom : DATE_MIN,
                        dateTo   != null ? dateTo   : DATE_MAX,
                        pageable)
                .stream()
                .map(ActivityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Activity> findByOrganizerId(String organizerId) {
        return jpaRepository.findByOrganizerId(organizerId).stream()
                .map(ActivityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Activity> findByParticipantId(String userId) {
        return jpaRepository.findByParticipantUserId(userId).stream()
                .map(ActivityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(ActivityId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public List<Activity> findByStatusInAndScheduledAtBefore(List<ActivityStatus> statuses, Instant cutoff) {
        var statusNames = statuses.stream().map(ActivityStatus::name).toList();
        return jpaRepository.findByStatusInAndScheduledAtBefore(statusNames, cutoff).stream()
                .map(ActivityMapper::toDomain)
                .toList();
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static String lower(String s) {
        return (s == null || s.isBlank()) ? null : s.toLowerCase();
    }
}
