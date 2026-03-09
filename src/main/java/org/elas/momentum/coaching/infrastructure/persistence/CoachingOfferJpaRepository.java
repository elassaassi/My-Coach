package org.elas.momentum.coaching.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CoachingOfferJpaRepository extends JpaRepository<CoachingOfferEntity, String> {

    List<CoachingOfferEntity> findByCoachId(String coachId);
}
