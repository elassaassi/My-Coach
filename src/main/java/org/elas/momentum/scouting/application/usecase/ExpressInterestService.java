package org.elas.momentum.scouting.application.usecase;

import org.elas.momentum.scouting.domain.model.ScoutingInterest;
import org.elas.momentum.scouting.domain.port.in.ExpressInterestUseCase;
import org.elas.momentum.scouting.domain.port.out.ScoutingInterestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExpressInterestService implements ExpressInterestUseCase {

    private final ScoutingInterestRepository interestRepository;

    public ExpressInterestService(ScoutingInterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    @Override
    public String express(Command command) {
        boolean alreadyExists = interestRepository.existsByRecruiterIdAndTalentId(
                command.recruiterId(), command.talentId());

        if (alreadyExists) {
            throw new IllegalStateException(
                    "Interest already expressed by recruiter " + command.recruiterId()
                            + " for talent " + command.talentId());
        }

        ScoutingInterest interest = ScoutingInterest.create(
                command.recruiterId(), command.talentId(), command.note());

        ScoutingInterest saved = interestRepository.save(interest);
        return saved.getId().value();
    }
}
