package org.elas.momentum.matching.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface MatchJpaRepository extends JpaRepository<MatchRequestEntity, String> {
    List<MatchRequestEntity> findByRequesterId(String requesterId);
}
