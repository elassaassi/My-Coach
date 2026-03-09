package org.elas.momentum.scouting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface TalentJpaRepository extends JpaRepository<TalentProfileEntity, String> {

    Optional<TalentProfileEntity> findByUserId(String userId);

    @Query("""
            SELECT t FROM TalentProfileEntity t
            WHERE t.sport = :sport
              AND t.proScore >= :minScore
              AND t.isVisible = true
            ORDER BY t.proScore DESC
            """)
    List<TalentProfileEntity> findBySportAndMinScore(
            @Param("sport") String sport,
            @Param("minScore") int minScore);
}
