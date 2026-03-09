package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.domain.exception.ActivityNotFoundException;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.port.in.JoinActivityUseCase;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.elas.momentum.activity.infrastructure.persistence.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JoinActivityService implements JoinActivityUseCase {

    private final ActivityRepository repository;

    public JoinActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public ActivityResult join(String activityId, String userId) {
        var activity = repository.findById(ActivityId.of(activityId))
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        activity.join(userId);
        return ActivityMapper.toResult(repository.save(activity));
    }

    @Override
    public ActivityResult leave(String activityId, String userId) {
        var activity = repository.findById(ActivityId.of(activityId))
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        activity.leave(userId);
        return ActivityMapper.toResult(repository.save(activity));
    }
}
