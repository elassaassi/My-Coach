package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.domain.exception.ActivityNotFoundException;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.port.in.DeleteActivityUseCase;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteActivityService implements DeleteActivityUseCase {

    private final ActivityRepository repository;

    public DeleteActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void delete(String activityId, String requesterId) {
        var activity = repository.findById(ActivityId.of(activityId))
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.getOrganizerId().equals(requesterId)) {
            throw new IllegalStateException("Only the organizer can delete this activity");
        }

        repository.deleteById(ActivityId.of(activityId));
    }
}