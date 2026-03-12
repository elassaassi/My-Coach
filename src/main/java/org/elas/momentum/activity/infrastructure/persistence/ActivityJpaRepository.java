package org.elas.momentum.activity.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

interface ActivityJpaRepository extends JpaRepository<ActivityEntity, String> {

    /** Single activity detail — load participants eagerly via EntityGraph. */
    @EntityGraph("Activity.withParticipants")
    Optional<ActivityEntity> findWithParticipantsById(String id);

    /** Organizer's activities — DISTINCT prevents duplicates when activity has multiple participants. */
    @Query("SELECT DISTINCT a FROM ActivityEntity a LEFT JOIN FETCH a.participants WHERE a.organizerId = :organizerId")
    List<ActivityEntity> findByOrganizerId(@Param("organizerId") String organizerId);

    /** Activities where user is a participant — single query, no N+1. */
    @EntityGraph("Activity.withParticipants")
    @Query("""
            SELECT DISTINCT a FROM ActivityEntity a
            JOIN a.participants p
            WHERE p.userId = :userId
            """)
    List<ActivityEntity> findByParticipantUserId(@Param("userId") String userId);

    /**
     * Flexible paginated search — EntityGraph ensures participants are loaded
     * in a single LEFT JOIN FETCH query instead of N+1 separate queries.
     */
    @EntityGraph("Activity.withParticipants")
    @Query("""
            SELECT DISTINCT a FROM ActivityEntity a
            WHERE (:sport    IS NULL OR LOWER(a.sport) = :sport)
              AND (:city     IS NULL OR LOWER(a.city)  = :city)
              AND (:status   IS NULL OR a.status        = :status)
              AND (:dateFrom IS NULL OR a.scheduledAt  >= :dateFrom)
              AND (:dateTo   IS NULL OR a.scheduledAt  <= :dateTo)
            ORDER BY a.scheduledAt ASC
            """)
    List<ActivityEntity> search(
            @Param("sport")    String sport,
            @Param("city")     String city,
            @Param("status")   String status,
            @Param("dateFrom") Instant dateFrom,
            @Param("dateTo")   Instant dateTo,
            Pageable pageable
    );

    /** Scheduler: find activities in given statuses whose scheduledAt is before the cutoff. */
    @Query("SELECT DISTINCT a FROM ActivityEntity a LEFT JOIN FETCH a.participants WHERE a.status IN :statuses AND a.scheduledAt < :cutoff")
    List<ActivityEntity> findByStatusInAndScheduledAtBefore(
            @Param("statuses") List<String> statuses,
            @Param("cutoff")   Instant cutoff
    );
}
