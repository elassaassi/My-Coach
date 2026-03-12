package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.domain.exception.ActivityNotFoundException;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.port.in.CompleteActivityUseCase;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.elas.momentum.activity.infrastructure.persistence.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompleteActivityService implements CompleteActivityUseCase {

    private final ActivityRepository repository;

    public CompleteActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public ActivityResult complete(String activityId, String requesterId) {
        var activity = repository.findById(ActivityId.of(activityId))
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        if (!activity.getOrganizerId().equals(requesterId)) {
            throw new IllegalStateException("Only the organizer can mark an activity as completed");
        }

        activity.complete();
        return ActivityMapper.toResult(repository.save(activity));
    }
}