package org.elas.momentum.activity.domain.port.in;

import org.elas.momentum.activity.application.dto.ActivityResult;

import java.time.Instant;
import java.util.List;

public interface GetActivityUseCase {
    ActivityResult getById(String activityId);
    List<ActivityResult> search(String sport, String city, String status, Instant dateFrom, Instant dateTo, int page, int size);
    List<ActivityResult> getByUser(String userId);
}
