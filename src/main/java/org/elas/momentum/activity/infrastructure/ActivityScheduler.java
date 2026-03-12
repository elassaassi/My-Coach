package org.elas.momentum.activity.infrastructure;

import org.elas.momentum.activity.domain.model.ActivityStatus;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Auto-transitions activity statuses based on scheduled time:
 *   OPEN / FULL  →  ONGOING   when scheduledAt is reached
 *   ONGOING      →  COMPLETED  2 h after scheduledAt (match presumed done)
 */
@Component
public class ActivityScheduler {

    private static final Logger log = LoggerFactory.getLogger(ActivityScheduler.class);

    private static final Duration MATCH_DURATION = Duration.ofHours(2);

    private final ActivityRepository activityRepository;

    public ActivityScheduler(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Scheduled(fixedDelay = 60_000)   // every minute
    @Transactional
    public void transitionActivities() {
        var now         = Instant.now();
        var completeCutoff = now.minus(MATCH_DURATION);

        // ── OPEN / FULL → ONGOING ────────────────────────────────────────────
        var toStart = activityRepository.findByStatusInAndScheduledAtBefore(
                List.of(ActivityStatus.OPEN, ActivityStatus.FULL), now);

        for (var activity : toStart) {
            try {
                activity.startOngoing();
                activityRepository.save(activity);
                log.info("Activity {} → ONGOING (scheduledAt={})", activity.getId().value(), activity.getScheduledAt());
            } catch (Exception e) {
                log.warn("Cannot start activity {}: {}", activity.getId().value(), e.getMessage());
            }
        }

        // ── ONGOING → COMPLETED (2 h after start) ───────────────────────────
        var toComplete = activityRepository.findByStatusInAndScheduledAtBefore(
                List.of(ActivityStatus.ONGOING), completeCutoff);

        for (var activity : toComplete) {
            try {
                activity.complete();
                activityRepository.save(activity);
                log.info("Activity {} → COMPLETED (scheduledAt={})", activity.getId().value(), activity.getScheduledAt());
            } catch (Exception e) {
                log.warn("Cannot complete activity {}: {}", activity.getId().value(), e.getMessage());
            }
        }
    }
}