package org.elas.momentum.coaching.application.usecase;

import org.elas.momentum.coaching.domain.model.Coach;
import org.elas.momentum.coaching.domain.port.in.SearchCoachesUseCase;
import org.elas.momentum.coaching.domain.port.out.CoachRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchCoachesService implements SearchCoachesUseCase {

    private final CoachRepository coachRepository;

    public SearchCoachesService(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    @Override
    public List<Coach> search(Query query) {
        return coachRepository.findAvailable(
                query.sport(),
                query.maxHourlyRate(),
                query.minRating()
        );
    }
}
