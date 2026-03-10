package org.elas.momentum.activity.infrastructure.persistence;

import org.elas.momentum.activity.domain.model.Activity;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

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
        // Try to load the existing entity to preserve participant BIGSERIAL ids.
        // Without this, toEntity() creates ParticipantEntity without id → Hibernate
        // tries to INSERT all participants again → unique constraint violation.
        ActivityEntity existing = jpaRepository.findById(activity.getId().value()).orElse(null);

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
        return jpaRepository.findById(id.value()).map(ActivityMapper::toDomain);
    }

    @Override
    public List<Activity> search(String sport, String city, String status, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpaRepository.search(lower(sport), lower(city), blankToNull(status), pageable)
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

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static String lower(String s) {
        return (s == null || s.isBlank()) ? null : s.toLowerCase();
    }
}
