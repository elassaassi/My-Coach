package org.elas.momentum.coaching.infrastructure.persistence;

import org.elas.momentum.coaching.domain.model.CoachingOffer;
import org.elas.momentum.coaching.domain.port.out.OfferRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class OfferPersistenceAdapter implements OfferRepository {

    private final CoachingOfferJpaRepository jpaRepository;

    OfferPersistenceAdapter(CoachingOfferJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(CoachingOffer offer) {
        jpaRepository.save(CoachingMapper.toEntity(offer));
    }

    @Override
    public Optional<CoachingOffer> findById(String id) {
        return jpaRepository.findById(id).map(CoachingMapper::toDomain);
    }

    @Override
    public List<CoachingOffer> findByCoachId(String coachId) {
        return jpaRepository.findByCoachId(coachId)
                .stream()
                .map(CoachingMapper::toDomain)
                .toList();
    }
}
