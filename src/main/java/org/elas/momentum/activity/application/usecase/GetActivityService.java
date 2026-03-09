package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.domain.exception.ActivityNotFoundException;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.port.in.GetActivityUseCase;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.elas.momentum.activity.infrastructure.persistence.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetActivityService implements GetActivityUseCase {

    private final ActivityRepository repository;

    public GetActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public ActivityResult getById(String activityId) {
        return repository.findById(ActivityId.of(activityId))
                .map(ActivityMapper::toResult)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
    }

    @Override
    public List<ActivityResult> search(String sport, String city, String status, int page, int size) {
        return repository.search(sport, city, status, page, size).stream()
                .map(ActivityMapper::toResult)
                .toList();
    }

    @Override
    public List<ActivityResult> getByUser(String userId) {
        var asOrganizer   = repository.findByOrganizerId(userId);
        var asParticipant = repository.findByParticipantId(userId);

        return java.util.stream.Stream.concat(asOrganizer.stream(), asParticipant.stream())
                .distinct()
                .map(ActivityMapper::toResult)
                .toList();
    }
}
