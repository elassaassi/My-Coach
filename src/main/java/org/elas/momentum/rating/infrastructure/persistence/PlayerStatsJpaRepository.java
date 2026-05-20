package org.elas.momentum.rating.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsJpaRepository extends JpaRepository<PlayerStatsEntity, String> {
    Optional<PlayerStatsEntity> findByUserIdAndSport(String userId, String sport);

    @Query("SELECT s FROM PlayerStatsEntity s WHERE s.sport = :sport OR s.sport = 'all' ORDER BY s.proScore DESC")
    List<PlayerStatsEntity> findTopBySport(@Param("sport") String sport, Pageable pageable);
}
