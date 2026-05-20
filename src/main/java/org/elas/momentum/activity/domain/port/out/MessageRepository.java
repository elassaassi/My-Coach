package org.elas.momentum.activity.domain.port.out;

import org.elas.momentum.activity.domain.model.ActivityMessage;

import java.util.List;

public interface MessageRepository {
    ActivityMessage save(ActivityMessage message);
    List<ActivityMessage> findByActivityId(String activityId, int limit);
}