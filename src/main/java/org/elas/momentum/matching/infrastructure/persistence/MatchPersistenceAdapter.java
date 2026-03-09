package org.elas.momentum.matching.infrastructure.persistence;

import org.elas.momentum.matching.domain.model.MatchRequest;
import org.elas.momentum.matching.domain.model.MatchRequestId;
import org.elas.momentum.matching.domain.port.out.MatchRequestRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class MatchPersistenceAdapter implements MatchRequestRepository {

    private final MatchJpaRepository jpaRepository;

    MatchPersistenceAdapter(MatchJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MatchRequest save(MatchRequest matchRequest) {
        return MatchMapper.toDomain(jpaRepository.save(MatchMapper.toEntity(matchRequest)));
    }

    @Override
    public Optional<MatchRequest> findById(MatchRequestId id) {
        return jpaRepository.findById(id.value()).map(MatchMapper::toDomain);
    }

    @Override
    public List<MatchRequest> findByRequesterId(String requesterId) {
        return jpaRepository.findByRequesterId(requesterId).stream()
                .map(MatchMapper::toDomain)
                .toList();
    }
}
