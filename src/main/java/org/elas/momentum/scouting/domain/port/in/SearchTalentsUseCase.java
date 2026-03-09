package org.elas.momentum.scouting.domain.port.in;

import org.elas.momentum.scouting.domain.model.TalentProfile;

import java.util.List;

public interface SearchTalentsUseCase {

    record Query(String sport, Integer minProScore, String region) {}

    List<TalentProfile> search(Query query);
}
