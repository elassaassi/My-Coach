package org.elas.momentum.activity.domain.port.in;

public interface DeleteActivityUseCase {
    void delete(String activityId, String requesterId);
}