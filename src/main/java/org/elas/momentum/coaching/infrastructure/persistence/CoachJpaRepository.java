package org.elas.momentum.coaching.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface CoachJpaRepository extends JpaRepository<CoachEntity, String> {

    Optional<CoachEntity> findByUserId(String userId);

    @Query("""
            SELECT DISTINCT c FROM CoachEntity c
            LEFT JOIN c.sports s
            WHERE c.isAvailable = true
            AND (:sport IS NULL OR s = :sport)
            AND (:maxHourlyRate IS NULL OR c.hourlyRate <= :maxHourlyRate)
            AND (:minRating IS NULL OR c.averageRating >= :minRating)
            """)
    List<CoachEntity> findAvailable(
            @Param("sport") String sport,
            @Param("maxHourlyRate") Double maxHourlyRate,
            @Param("minRating") Double minRating
    );
}
