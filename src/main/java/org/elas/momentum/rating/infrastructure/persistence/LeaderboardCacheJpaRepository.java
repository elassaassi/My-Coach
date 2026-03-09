package org.elas.momentum.rating.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaderboardCacheJpaRepository
        extends JpaRepository<LeaderboardCacheEntity, LeaderboardCacheEntity.LeaderboardId> {

    @Query("SELECT l FROM LeaderboardCacheEntity l WHERE l.id.sport = :sport ORDER BY l.score DESC")
    List<LeaderboardCacheEntity> findBySportOrderByScoreDesc(@Param("sport") String sport, Pageable pageable);

    @Modifying
    @Query("DELETE FROM LeaderboardCacheEntity l WHERE l.id.sport = :sport")
    void deleteBySport(@Param("sport") String sport);
}
