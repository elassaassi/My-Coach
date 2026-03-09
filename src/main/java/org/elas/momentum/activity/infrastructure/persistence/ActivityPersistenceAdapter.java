package org.elas.momentum.activity.infrastructure.persistence;

import org.elas.momentum.activity.domain.model.Activity;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.model.ActivityStatus;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class ActivityPersistenceAdapter implements ActivityRepository {

    private final ActivityJpaRepository jpaRepository;

    ActivityPersistenceAdapter(ActivityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Activity save(Activity activity) {
        return ActivityMapper.toDomain(jpaRepository.save(ActivityMapper.toEntity(activity)));
    }

    @Override
    public Optional<Activity> findById(ActivityId id) {
        return jpaRepository.findById(id.value()).map(ActivityMapper::toDomain);
    }

    @Override
    public List<Activity> findByStatus(ActivityStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
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
}
