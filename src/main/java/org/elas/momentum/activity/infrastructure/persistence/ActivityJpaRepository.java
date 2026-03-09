package org.elas.momentum.activity.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ActivityJpaRepository extends JpaRepository<ActivityEntity, String> {

    List<ActivityEntity> findByOrganizerId(String organizerId);

    @Query("""
            SELECT DISTINCT a FROM ActivityEntity a
            JOIN a.participants p
            WHERE p.userId = :userId
            """)
    List<ActivityEntity> findByParticipantUserId(@Param("userId") String userId);

    /**
     * Flexible search — all params are optional (null = no filter).
     * status defaults to OPEN when null is passed from the service.
     */
    @Query("""
            SELECT a FROM ActivityEntity a
            WHERE (:sport  IS NULL OR LOWER(a.sport)  = LOWER(:sport))
              AND (:city   IS NULL OR LOWER(a.city)   = LOWER(:city))
              AND (:status IS NULL OR a.status         = :status)
            ORDER BY a.scheduledAt ASC
            """)
    List<ActivityEntity> search(
            @Param("sport")  String sport,
            @Param("city")   String city,
            @Param("status") String status,
            Pageable pageable
    );
}
