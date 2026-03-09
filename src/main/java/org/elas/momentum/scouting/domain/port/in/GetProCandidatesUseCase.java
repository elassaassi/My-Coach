package org.elas.momentum.scouting.domain.port.in;

import org.elas.momentum.scouting.domain.model.TalentProfile;

import java.util.List;

public interface GetProCandidatesUseCase {

    List<TalentProfile> getProCandidates(String sport, int minScore);
}
