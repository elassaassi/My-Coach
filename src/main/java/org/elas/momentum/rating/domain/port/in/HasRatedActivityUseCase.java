package org.elas.momentum.rating.domain.port.in;

public interface HasRatedActivityUseCase {
    boolean hasRated(String activityId, String raterId);
}