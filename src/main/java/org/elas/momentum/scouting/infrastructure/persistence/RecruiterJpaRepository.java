package org.elas.momentum.scouting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface RecruiterJpaRepository extends JpaRepository<RecruiterProfileEntity, String> {

    Optional<RecruiterProfileEntity> findByUserId(String userId);
}
