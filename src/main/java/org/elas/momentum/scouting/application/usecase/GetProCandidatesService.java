package org.elas.momentum.scouting.application.usecase;

import org.elas.momentum.scouting.domain.model.TalentProfile;
import org.elas.momentum.scouting.domain.port.in.GetProCandidatesUseCase;
import org.elas.momentum.scouting.domain.port.out.TalentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetProCandidatesService implements GetProCandidatesUseCase {

    private final TalentRepository talentRepository;

    public GetProCandidatesService(TalentRepository talentRepository) {
        this.talentRepository = talentRepository;
    }

    @Override
    public List<TalentProfile> getProCandidates(String sport, int minScore) {
        return talentRepository.findBySportAndMinScore(sport, minScore);
    }
}
