package org.elas.momentum.activity.domain.port.in;

import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.application.dto.CreateActivityCommand;

public interface CreateActivityUseCase {
    ActivityResult create(String organizerId, CreateActivityCommand command);
}
