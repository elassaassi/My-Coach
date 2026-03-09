package org.elas.momentum.activity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ActivityJpaRepository extends JpaRepository<ActivityEntity, String> {

    List<ActivityEntity> findByStatus(String status);

    List<ActivityEntity> findByOrganizerId(String organizerId);

    @Query("""
            SELECT DISTINCT a FROM ActivityEntity a
            JOIN a.participants p
            WHERE p.userId = :userId
            """)
    List<ActivityEntity> findByParticipantUserId(@Param("userId") String userId);
}
