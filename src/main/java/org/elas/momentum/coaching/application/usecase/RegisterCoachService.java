package org.elas.momentum.coaching.application.usecase;

import org.elas.momentum.coaching.domain.model.Coach;
import org.elas.momentum.coaching.domain.port.in.RegisterCoachUseCase;
import org.elas.momentum.coaching.domain.port.out.CoachRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterCoachService implements RegisterCoachUseCase {

    private final CoachRepository coachRepository;

    public RegisterCoachService(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    @Override
    public String register(Command command) {
        Coach coach = Coach.register(
                command.userId(),
                command.bio(),
                command.hourlyRate(),
                command.currency()
        );
        coachRepository.save(coach);
        return coach.getId().value();
    }
}
