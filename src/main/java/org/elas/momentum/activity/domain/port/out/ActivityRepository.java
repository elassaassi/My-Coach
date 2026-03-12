package org.elas.momentum.activity.domain.port.out;

import org.elas.momentum.activity.domain.model.Activity;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.model.ActivityStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    Activity save(Activity activity);
    Optional<Activity> findById(ActivityId id);
    List<Activity> search(String sport, String city, String status, Instant dateFrom, Instant dateTo, int page, int size);
    List<Activity> findByOrganizerId(String organizerId);
    List<Activity> findByParticipantId(String userId);
    void deleteById(ActivityId id);
    /** Used by the scheduler to auto-transition activities past their scheduled time. */
    List<Activity> findByStatusInAndScheduledAtBefore(List<ActivityStatus> statuses, Instant cutoff);
}
