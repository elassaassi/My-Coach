package org.elas.momentum.activity.domain.port.in;

import org.elas.momentum.activity.application.dto.ActivityResult;

import java.util.List;

public interface GetActivityUseCase {
    ActivityResult getById(String activityId);
    List<ActivityResult> getOpenBySport(String sport, double lat, double lon, int radiusKm);
    List<ActivityResult> getByUser(String userId);
}
