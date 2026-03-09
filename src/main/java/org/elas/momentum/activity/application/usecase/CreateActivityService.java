package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.application.dto.CreateActivityCommand;
import org.elas.momentum.activity.domain.model.Activity;
import org.elas.momentum.activity.domain.model.Location;
import org.elas.momentum.activity.domain.port.in.CreateActivityUseCase;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.elas.momentum.activity.infrastructure.persistence.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateActivityService implements CreateActivityUseCase {

    private final ActivityRepository repository;

    public CreateActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public ActivityResult create(String organizerId, CreateActivityCommand command) {
        var location = new Location(
                command.latitude(),
                command.longitude(),
                command.venueName() != null ? command.venueName() : "",
                command.city(),
                command.country() != null ? command.country() : ""
        );

        Activity activity = Activity.create(
                organizerId,
                command.title(),
                command.sport(),
                command.requiredLevel(),
                location,
                command.scheduledAt(),
                command.maxParticipants()
        );
        activity.setDescription(command.description());

        return ActivityMapper.toResult(repository.save(activity));
    }
}
