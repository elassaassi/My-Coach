package org.elas.momentum.coaching.infrastructure.persistence;

import org.elas.momentum.coaching.domain.model.Coach;
import org.elas.momentum.coaching.domain.port.out.CoachRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class CoachPersistenceAdapter implements CoachRepository {

    private final CoachJpaRepository jpaRepository;

    CoachPersistenceAdapter(CoachJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Coach coach) {
        jpaRepository.save(CoachingMapper.toEntity(coach));
    }

    @Override
    public Optional<Coach> findById(String id) {
        return jpaRepository.findById(id).map(CoachingMapper::toDomain);
    }

    @Override
    public Optional<Coach> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId).map(CoachingMapper::toDomain);
    }

    @Override
    public List<Coach> findAvailable(String sport, Double maxHourlyRate, Double minRating) {
        return jpaRepository.findAvailable(sport, maxHourlyRate, minRating)
                .stream()
                .map(CoachingMapper::toDomain)
                .toList();
    }
}
