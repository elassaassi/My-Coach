package org.elas.momentum.scouting.domain.port.out;

import org.elas.momentum.scouting.domain.model.TalentProfile;

import java.util.List;
import java.util.Optional;

public interface TalentRepository {

    TalentProfile save(TalentProfile talent);

    Optional<TalentProfile> findByUserId(String userId);

    List<TalentProfile> findBySportAndMinScore(String sport, int minScore);
}
