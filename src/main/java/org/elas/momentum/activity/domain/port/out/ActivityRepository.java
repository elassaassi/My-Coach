package org.elas.momentum.activity.domain.port.out;

import org.elas.momentum.activity.domain.model.Activity;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.model.ActivityStatus;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    Activity save(Activity activity);
    Optional<Activity> findById(ActivityId id);
    List<Activity> findByStatus(ActivityStatus status);
    List<Activity> findByOrganizerId(String organizerId);
    List<Activity> findByParticipantId(String userId);
}
