package org.elas.momentum.coaching.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CoachingBookingJpaRepository extends JpaRepository<CoachingBookingEntity, String> {

    List<CoachingBookingEntity> findByClientId(String clientId);
}
