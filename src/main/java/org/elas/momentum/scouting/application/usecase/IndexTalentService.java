package org.elas.momentum.scouting.application.usecase;

import org.elas.momentum.scouting.domain.model.TalentProfile;
import org.elas.momentum.scouting.domain.port.in.IndexTalentUseCase;
import org.elas.momentum.scouting.domain.port.out.TalentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IndexTalentService implements IndexTalentUseCase {

    private final TalentRepository talentRepository;

    public IndexTalentService(TalentRepository talentRepository) {
        this.talentRepository = talentRepository;
    }

    @Override
    public void index(String userId, String sport, int proScore) {
        TalentProfile profile = talentRepository.findByUserId(userId)
                .orElseGet(() -> TalentProfile.create(userId, sport, proScore));

        profile.updateScore(proScore);
        talentRepository.save(profile);
    }
}
