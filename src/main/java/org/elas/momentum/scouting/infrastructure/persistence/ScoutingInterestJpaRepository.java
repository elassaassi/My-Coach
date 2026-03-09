package org.elas.momentum.scouting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ScoutingInterestJpaRepository extends JpaRepository<ScoutingInterestEntity, String> {

    List<ScoutingInterestEntity> findByRecruiterId(String recruiterId);

    boolean existsByRecruiterIdAndTalentId(String recruiterId, String talentId);
}
