package org.elas.momentum.rating.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerStatsJpaRepository extends JpaRepository<PlayerStatsEntity, String> {
    Optional<PlayerStatsEntity> findByUserIdAndSport(String userId, String sport);
}
