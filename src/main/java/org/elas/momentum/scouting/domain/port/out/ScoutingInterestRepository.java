package org.elas.momentum.scouting.domain.port.out;

import org.elas.momentum.scouting.domain.model.ScoutingInterest;

import java.util.List;

public interface ScoutingInterestRepository {

    ScoutingInterest save(ScoutingInterest interest);

    List<ScoutingInterest> findByRecruiterId(String recruiterId);

    boolean existsByRecruiterIdAndTalentId(String recruiterId, String talentId);
}
